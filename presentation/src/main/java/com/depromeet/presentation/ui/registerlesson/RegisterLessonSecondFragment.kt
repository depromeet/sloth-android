package com.depromeet.presentation.ui.registerlesson

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentRegisterLessonSecondBinding
import com.depromeet.presentation.extensions.hideKeyBoard
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseFragment
import com.depromeet.presentation.ui.registerlesson.RegisterLessonViewModel.Companion.CUSTOM_SETTING
import com.depromeet.presentation.ui.registerlesson.RegisterLessonViewModel.Companion.ONE_MONTH
import com.depromeet.presentation.ui.registerlesson.RegisterLessonViewModel.Companion.ONE_WEEK
import com.depromeet.presentation.ui.registerlesson.RegisterLessonViewModel.Companion.THREE_MONTH
import com.depromeet.presentation.ui.registerlesson.RegisterLessonViewModel.Companion.TWO_MONTH
import com.depromeet.presentation.util.CALENDAR_TAG
import com.depromeet.presentation.util.CALENDAR_TIME_ZONE
import com.depromeet.presentation.util.DECIMAL_FORMAT_PATTERN
import com.depromeet.presentation.util.DEFAULT_STRING_VALUE
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


@AndroidEntryPoint
class RegisterLessonSecondFragment : BaseFragment<FragmentRegisterLessonSecondBinding>(R.layout.fragment_register_lesson_second) {

    private val viewModel: RegisterLessonViewModel by hiltNavGraphViewModels(R.id.nav_register_lesson)

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
            vm = viewModel
        }
        initViews()
        initListener()
        initObserver()
    }

    private fun initListener() {
        binding.tbLayout.tbRegisterLesson.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.registerLessonStartDateEvent.collect {
                    showLessonStartDateCalendar()
                }
            }

            launch {
                viewModel.registerLessonEndDateEvent.collect {
                    if (viewModel.lessonEndDateSelectedItemPosition.value == CUSTOM_SETTING) {
                        showLessonEndDateCalendar()
                    }
                }
            }

            launch {
                viewModel.lessonDateRangeValidation.collect { isEnable ->
                    if (!isEnable && viewModel.lessonEndDateSelectedItemPosition.value == CUSTOM_SETTING) {
                        Toast.makeText(requireContext(), getString(R.string.lesson_date_range_validation_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            launch {
                viewModel.navigateToRegisterLessonCheckEvent.collect {
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

    @SuppressLint("NewApi")
    private fun showLessonStartDateCalendar() = with(binding) {
        val materialDateBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(getString(R.string.lesson_start_date))
        }
        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener { utcMillis ->
                val startDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcMillis), ZoneId.of(CALENDAR_TIME_ZONE))
                viewModel.setLessonStartDate(startDate)
                // 강의 시작일이 변하면 직접 설정이 아닌 경우엔 완강 목표일도 갱신되어야 한다.
                viewModel.setLessonEndDateBySpinner()
            }
        }
        materialDatePicker.show(childFragmentManager, CALENDAR_TAG)
    }

    @SuppressLint("NewApi")
    private fun showLessonEndDateCalendar() = with(binding) {
        val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(viewModel.lessonStartDate.value.plusDays(1).toInstant().toEpochMilli()))

        val materialDateBuilder = MaterialDatePicker.Builder.datePicker().apply {
                setCalendarConstraints(constraintsBuilder.build())
                setTitleText(getString(R.string.lesson_finish_date))
            }
        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener { utcMillis ->
                val endDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcMillis), ZoneId.of(CALENDAR_TIME_ZONE))
                viewModel.setLessonEndDateByCalendar(endDate)
            }
        }
        materialDatePicker.show(childFragmentManager, CALENDAR_TAG)
    }

    private fun bindAdapter() = with(binding) {
        lessonEndDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRegisterLessonEndDate.apply {
            adapter = lessonEndDateAdapter
            setSelection(viewModel.lessonEndDateSelectedItemPosition.value, false)
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
                viewModel.setLessonEndDateSelectedItemPosition(
                    spnRegisterLessonEndDate.selectedItemPosition
                )
                when (spnRegisterLessonEndDate.selectedItemPosition) {
                    ONE_WEEK, ONE_MONTH, TWO_MONTH, THREE_MONTH -> {
                        viewModel.setLessonEndDateBySpinner()
                    }
                    CUSTOM_SETTING -> viewModel.registerLessonEndDate()
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
                    viewModel.setLessonPrice(
                        charSequence.toString().replace(",", "").toInt()
                    )
                    result = decimalFormat.format(charSequence.toString().replace(",", "").toDouble())
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
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
                tvRegisterLessonPriceInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
                tvRegisterLessonPriceInfo.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
            }
        }
    }

    private fun focusInputFormOptional(editText: EditText) = with(binding) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(text: CharSequence?, i1: Int, i2: Int, i3: Int) {
                viewModel.setLessonMessage(text.toString())
            }
            override fun afterTextChanged(editable: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_lesson_rounded_edit_text_gray)
            }
        }
    }
}