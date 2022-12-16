package com.depromeet.sloth.ui.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonSecondBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.CUSTOM_SETTING
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.DAY
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.ONE_MONTH
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.ONE_WEEK
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.THREE_MONTH
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.TWO_MONTH
import com.depromeet.sloth.util.CALENDAR_TAG
import com.depromeet.sloth.util.CALENDAR_TIME_ZONE
import com.depromeet.sloth.util.DECIMAL_FORMAT_PATTERN
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class RegisterLessonSecondFragment :
    BaseFragment<FragmentRegisterLessonSecondBinding>(R.layout.fragment_register_lesson_second) {

    private val registerLessonViewModel: RegisterLessonViewModel by activityViewModels()

    private val lessonEndDateAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner,
            resources.getStringArray(R.array.lesson_end_date_array)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = registerLessonViewModel
        }
        initViews()
        initObserver()
    }

    private fun initObserver() = with(registerLessonViewModel) {
        repeatOnStarted {
            launch {
                registerLessonStartDateEvent
                    .collect {
                        registerLessonStartDate()
                    }
            }

            launch {
                registerLessonEndDateEvent
                    .collect {
                        registerLessonEndDateByCalendar()
                    }
            }

            launch {
                lessonDateRangeValidation
                    .collect { isEnable ->
                        when (isEnable) {
                            false -> showToast(getString(R.string.lesson_start_date_is_later_than_lesson_finish_date))
                            else -> Unit
                        }
                    }
            }

            launch {
                navigateToRegisterLessonCheckEvent
                    .collect {
                        registerLessonViewModel.setLessonInfo()
                        val action = RegisterLessonSecondFragmentDirections.actionRegisterLessonSecondToRegisterLessonCheck()
                        findNavController().safeNavigate(action)
                    }
            }
        }
    }

    override fun initViews() = with(binding) {
        bindAdapter()
        validateInputForm(etRegisterLessonPrice)
        focusInputFormOptional(etRegisterLessonMessage)
    }

    private fun registerLessonStartDate() = with(binding) {
        val materialDateBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(getString(R.string.lesson_start_date))
        }

        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone(CALENDAR_TIME_ZONE))
                calendar.time = Date(it)
                registerLessonViewModel.setLessonStartDate(calendar)
                // 강의 시작일이 변하면 직접 설정이 아닌 경우엔 완강 목표일도 갱신되어야 한다.
                updateLessonEndDate()
            }
        }
        materialDatePicker.show(childFragmentManager, CALENDAR_TAG)
    }

    private fun updateLessonEndDate() {
        registerLessonViewModel.setLessonEndDateBySpinner(registerLessonViewModel.lessonEndDateSelectedItemPosition.value)
    }

    private fun registerLessonEndDateByCalendar() = with(binding) {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(registerLessonViewModel.startDate.value.time + DAY))

        val materialDateBuilder =
            MaterialDatePicker.Builder.datePicker().apply {
                setCalendarConstraints(constraintsBuilder.build())
                setTitleText(getString(R.string.lesson_finish_date))
            }

        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone(CALENDAR_TIME_ZONE))
                calendar.time = Date(it)
                registerLessonViewModel.setLessonEndDateByCalendar(calendar)
            }
        }
        materialDatePicker.show(childFragmentManager, CALENDAR_TAG)
    }

    private fun bindAdapter() = with(binding) {
        lessonEndDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRegisterLessonEndDate.apply {
            adapter = lessonEndDateAdapter
            setSelection(registerLessonViewModel.lessonEndDateSelectedItemPosition.value, false)
            setSpinnerListener(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSpinnerListener(spinner: Spinner) = with(binding) {
        spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyBoard(requireActivity())
            }
            false
        }

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                registerLessonViewModel.setLessonEndDateSelectedItemPosition(
                    spnRegisterLessonEndDate.selectedItemPosition
                )

                when (spnRegisterLessonEndDate.selectedItemPosition) {
                    ONE_WEEK, ONE_MONTH, TWO_MONTH, THREE_MONTH -> {
                        registerLessonViewModel.setLessonEndDateBySpinner(spnRegisterLessonEndDate.selectedItemPosition)
                    }
                    CUSTOM_SETTING -> registerLessonViewModel.registerLessonEndDate()
                }
                binding.clRegisterLessonSecond.clearFocus()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun validateInputForm(editText: EditText) = with(binding) {
        var result = DEFAULT_STRING_VALUE
        val decimalFormat = DecimalFormat(DECIMAL_FORMAT_PATTERN)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i1: Int,
                i2: Int,
                i3: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    registerLessonViewModel.setLessonPrice(
                        charSequence.toString().replace(",", "").toInt()
                    )
                    result =
                        decimalFormat.format(charSequence.toString().replace(",", "").toDouble())
                    editText.setText(result)
                    editText.setSelection(result.length)

                    tvRegisterLessonPriceInfo.apply {
                        text = getString(R.string.input_lesson_price, result)
                        visibility = View.VISIBLE
                    }
                }

                if (TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    result = DEFAULT_STRING_VALUE
                    editText.setText(result)
                    tvRegisterLessonPriceInfo.apply {
                        text = result
                        visibility = View.INVISIBLE
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                tvRegisterLessonPriceInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
                tvRegisterLessonPriceInfo.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }

    private fun focusInputFormOptional(editText: EditText) = with(binding) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
                registerLessonViewModel.setLessonMessage(text.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }
}