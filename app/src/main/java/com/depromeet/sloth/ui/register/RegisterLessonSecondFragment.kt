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
import com.depromeet.sloth.ui.common.EventObserver
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.CUSTOM_SETTING
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.DAY
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.DEFAULT_STRING_VALUE
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.ONE_MONTH
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.ONE_WEEK
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.THREE_MONTH
import com.depromeet.sloth.ui.register.RegisterLessonViewModel.Companion.TWO_MONTH
import com.depromeet.sloth.util.CALENDAR_TAG
import com.depromeet.sloth.util.CALENDAR_TIME_ZONE
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*

@AndroidEntryPoint
class RegisterLessonSecondFragment :
    BaseFragment<FragmentRegisterLessonSecondBinding>(R.layout.fragment_register_lesson_second) {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

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

        initObserver()
        initViews()
        initNavigation()
    }

    private fun initObserver() {
        viewModel.apply {
            navigateToStartDate.observe(viewLifecycleOwner, EventObserver {
                registerStartLessonDate()
            })

            navigateToEndDate.observe(viewLifecycleOwner, EventObserver {
                registerLessonEndDate()
            })

            lessonEndDateSelectedItemPosition.observe(viewLifecycleOwner) { position ->
                when (position) {
                    0 -> {
                        lockButton(binding.btnRegisterLesson, requireContext())
                    }
                    else -> {
                        unlockButton(binding.btnRegisterLesson, requireContext())
                    }
                }
            }

            lessonDateValidation.observe(viewLifecycleOwner) { isEnable ->
                when (isEnable) {
                    false -> {
                        showToast(getString(R.string.lesson_start_date_is_later_than_lesson_finish_date))
                        lockButton(binding.btnRegisterLesson, requireContext())
                    }

                    true -> {
                        unlockButton(binding.btnRegisterLesson, requireContext())
                    }
                }
            }
        }
    }

    private fun initNavigation() {
        viewModel.moveRegisterLessonCheckEvent.observe(viewLifecycleOwner, EventObserver {
            viewModel.setLessonInfo()
            moveRegisterLessonCheck()
        })
    }

    override fun initViews() = with(binding) {
        bindAdapter()

        validateInputForm(etRegisterLessonPrice)
        focusInputFormOptional(etRegisterLessonMessage)
    }

    private fun moveRegisterLessonCheck() {
        findNavController().navigate(R.id.action_register_lesson_second_to_register_lesson_check)
    }

    private fun registerStartLessonDate() = with(binding) {
        val materialDateBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(getString(R.string.lesson_start_date))
        }

        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone(CALENDAR_TIME_ZONE))
                calendar.time = Date(it)
                viewModel.updateLessonStartDate(calendar)
                viewModel.updateLessonEndDateBySpinner(viewModel.lessonEndDateSelectedItemPosition.value!!)
            }
        }
        materialDatePicker.show(childFragmentManager, CALENDAR_TAG)
    }

    private fun registerLessonEndDate() = with(binding) {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(viewModel.startDate.value!!.time + DAY))

        val materialDateBuilder =
            MaterialDatePicker.Builder.datePicker().apply {
                setCalendarConstraints(constraintsBuilder.build())
                setTitleText(getString(R.string.lesson_finish_date))
            }

        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone(CALENDAR_TIME_ZONE))
                calendar.time = Date(it)
                viewModel.updateLessonEndDateByCalendar(calendar)
            }
        }
        materialDatePicker.show(childFragmentManager, CALENDAR_TAG)
    }

    private fun bindAdapter() = with(binding) {
        lessonEndDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnRegisterGoalLessonDate.apply {
            adapter = lessonEndDateAdapter
            setSelection(viewModel.lessonEndDateSelectedItemPosition.value!!, false)
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
                clearFocus(etRegisterLessonPrice)
                clearFocus(etRegisterLessonMessage)
                Timber.d("${spnRegisterGoalLessonDate.selectedItemPosition}")

                viewModel.setLessonEndDateSelectedItemPosition(spnRegisterGoalLessonDate.selectedItemPosition)

                when (spnRegisterGoalLessonDate.selectedItemPosition) {
                    ONE_WEEK, ONE_MONTH, TWO_MONTH, THREE_MONTH -> {
                        viewModel.updateLessonEndDateBySpinner(spnRegisterGoalLessonDate.selectedItemPosition)
                    }

                    CUSTOM_SETTING -> {
                        viewModel.navigateToEndDate()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun validateInputForm(editText: EditText) = with(binding) {
        var result = DEFAULT_STRING_VALUE
        val decimalFormat = DecimalFormat("#,###")

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i1: Int,
                i2: Int,
                i3: Int,
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result) {
                    viewModel.setLessonPrice(charSequence.toString().replace(",", "").toInt())
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
                viewModel.setLessonMessage(text.toString())
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