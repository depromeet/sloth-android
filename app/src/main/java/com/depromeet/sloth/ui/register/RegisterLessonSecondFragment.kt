package com.depromeet.sloth.ui.register

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonSecondBinding
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_CATEGORY_NAME
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_TOTAL_NUMBER
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_NAME
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_SITE_NAME
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.view.LayoutInflater

import android.widget.TextView

import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.view.MotionEvent
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.extensions.*

class RegisterLessonSecondFragment : BaseFragment<FragmentRegisterLessonSecondBinding>() {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

    companion object {
        private const val DAY = 86400000L

        private const val DEFAULT = 0
        private const val ONE_WEEK = 1
        private const val ONE_MONTH = 2
        private const val TWO_MONTH = 3
        private const val THREE_MONTH = 4
        private const val CUSTOM_SETTING = 5

        const val LESSON_START_DATE = "lessonStartDate"
        const val LESSON_END_DATE = "lessonGoalDate"
        const val LESSON_PRICE = "lessonPrice"
        const val LESSON_ALERT_DAYS = "lessonPushNotiCycle"
        const val LESSON_MESSAGE = "lessonMessage"
    }

    lateinit var lessonName: String
    lateinit var lessoTotalNumber: Number
    lateinit var lessonCategoryName: String
    lateinit var lessonSiteName: String
    lateinit var lessonPrice: Number

    private var startDay: Long? = null
    private var endDay: Long? = null

    lateinit var startDate: Date

    lateinit var lessonStartDate: String
    lateinit var lessonEndDate: String

    private var isLessonGoalDateDecided = false
    //private var isLessonGoalDateSpnDecided = true

    lateinit var selectedItem: Number

    lateinit var lessonEndDateAdapter: ArrayAdapter<String>

    private val today = Date()

    lateinit var calendar: Calendar

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentRegisterLessonSecondBinding {
        return FragmentRegisterLessonSecondBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            lessonName = getString(LESSON_NAME).toString()
            lessoTotalNumber = getInt(LESSON_TOTAL_NUMBER)
            lessonCategoryName = getString(LESSON_CATEGORY_NAME).toString()
            lessonSiteName = getString(LESSON_SITE_NAME).toString()
        }
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() = with(binding) {
        super.onStart()
        if (isLessonGoalDateDecided) {
            tvRegisterGoalLessonDateInfo.visibility = View.VISIBLE
            tvRegisterGoalLessonDateInfo.text = changeDateFormat(lessonEndDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        if (::lessonEndDateAdapter.isInitialized.not()) {
            bindAdapter()
        }

        calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

        bindSpinner(spnRegisterGoalLessonDate, calendar)

        lockButton(btnRegisterLesson, requireContext())

        validateInputForm(etRegisterLessonPrice, btnRegisterLesson)
        focusInputFormOptional(etRegisterLessonMessage)

        if (::lessonStartDate.isInitialized.not()) {
            calendar.time = today
            calendarToText(LESSON_START_DATE, calendar, tvRegisterStartLessonDateInfo)
        } else {
            tvRegisterStartLessonDateInfo.text = changeDateFormat(lessonStartDate)
        }

        if (::selectedItem.isInitialized) {
            spnRegisterGoalLessonDate.setSelection(selectedItem as Int, false)
        } else {
            spnRegisterGoalLessonDate.setSelection(0, false)
        }

        tvRegisterStartLessonDateInfo.setOnClickListener {
            registerStartLessonDate(calendar)
        }

        tvRegisterGoalLessonDateInfo.setOnClickListener {
            clearFocus(binding.etRegisterLessonPrice)

            if (selectedItem == CUSTOM_SETTING) {
                registerGoalLessonDate(calendar)
            } else {
                return@setOnClickListener
            }
        }

        btnRegisterLesson.setOnClickListener {
            clearFocus(etRegisterLessonPrice)

            if (::selectedItem.isInitialized.not() || selectedItem == DEFAULT) {
                Toast.makeText(requireContext(),
                    "완강 목표일을 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etRegisterLessonPrice.text.toString().isEmpty()) {
                Toast.makeText(requireContext(),
                    "강의 금액을 입력해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (startDay!! >= endDay!!) {
                Toast.makeText(requireContext(),
                    "강의 시작일은 완강 목표일 이전이어야 해요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val args = Bundle().apply {
                putString(LESSON_NAME, lessonName)
                putInt(LESSON_TOTAL_NUMBER, lessoTotalNumber as Int)
                putString(LESSON_CATEGORY_NAME, lessonCategoryName)
                putString(LESSON_SITE_NAME, lessonSiteName)
                putString(LESSON_START_DATE, lessonStartDate)
                putString(LESSON_END_DATE, lessonEndDate)
                putInt(LESSON_PRICE, lessonPrice.toInt())
                putString(LESSON_MESSAGE, etRegisterLessonMessage.text.toString())
            }

            (activity as RegisterLessonActivity).changeFragment(
                (activity as RegisterLessonActivity).registerLessonCheckFragment, args)
            //moveRegisterLessonCheck()
        }
    }

    private fun moveRegisterLessonCheck() = with(binding) {
        findNavController().navigate(
            R.id.action_register_lesson_second_to_register_lesson_check, bundleOf(
                //"key" to "value"
                LESSON_NAME to lessonName,
                LESSON_TOTAL_NUMBER to lessoTotalNumber as Int,
                LESSON_CATEGORY_NAME to lessonCategoryName,
                LESSON_SITE_NAME to lessonSiteName,
                LESSON_START_DATE to lessonStartDate,
                LESSON_END_DATE to lessonEndDate,
                LESSON_PRICE to lessonPrice.toInt(),
                LESSON_MESSAGE to  etRegisterLessonMessage.text.toString()
            )
        )
    }

    private fun isStartDateInitialized(calendar: Calendar) {
        if (::startDate.isInitialized) {
            calendar.time = startDate
        } else {
            calendar.time = today
        }
    }

    private fun decideLessonGoalDate(flag: Boolean) = with(binding) {
        if (flag) {
            tvRegisterGoalLessonDateInfo.visibility = View.VISIBLE
            unlockButton(btnRegisterLesson,requireContext())
            isLessonGoalDateDecided = true
        } else {
            tvRegisterGoalLessonDateInfo.visibility = View.GONE
            lockButton(btnRegisterLesson, requireContext())
            isLessonGoalDateDecided = false
        }
    }

    private fun registerStartLessonDate(calendar: Calendar) = with(binding) {
        val materialDateBuilder = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(getString(R.string.lesson_start_date))
        }

        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener {
                startDate = Date(it)
                calendar.time = Date(it)
                calendarToText(LESSON_START_DATE, calendar, tvRegisterStartLessonDateInfo)

                if (isLessonGoalDateDecided) {
                    updateGoalLessonDate(calendar)
                }
            }
        }
        materialDatePicker.show(childFragmentManager, "calendar")
    }

    private fun calendarToText(date: String, calendar: Calendar, textView: TextView? = null) =
        with(binding) {
            if (date == LESSON_START_DATE) {
                startDay = calendar.timeInMillis
                lessonStartDate = getPickerDateToDash(calendar.time)
            } else {
                endDay = calendar.timeInMillis
                lessonEndDate = getPickerDateToDash(calendar.time)
            }
            if (textView != null) {
                textView.text = getPickerDateToDot(calendar.time)
            }
        }

    private fun updateGoalLessonDate(calendar: Calendar) = with(binding) {
        when (selectedItem) {
            ONE_WEEK -> {
                calendar.add(Calendar.DATE, 7)
                calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
            }

            ONE_MONTH -> {
                calendar.add(Calendar.MONTH, 1)
                calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
            }

            TWO_MONTH -> {
                calendar.add(Calendar.MONTH, 2)
                calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
            }

            THREE_MONTH -> {
                calendar.add(Calendar.MONTH, 3)
                calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
            }

            else -> Unit
        }
    }

    private fun registerGoalLessonDate(calendar: Calendar) = with(binding) {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(startDay!! + DAY))

        val materialDateBuilder =
            MaterialDatePicker.Builder.datePicker().apply {
                setCalendarConstraints(constraintsBuilder.build())
                setTitleText(getString(R.string.lesson_finish_date))
            }

        val materialDatePicker = materialDateBuilder.build().apply {
            addOnPositiveButtonClickListener {
                calendar.time = Date(it)
                calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
                decideLessonGoalDate(true)
            }
        }
        materialDatePicker.show(childFragmentManager, "calendar")
    }

    private fun bindAdapter() {
        lessonEndDateAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            resources.getStringArray(R.array.lesson_goal_date_array)
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun bindSpinner(spinner: Spinner, calendar: Calendar) {
        spinner.adapter = lessonEndDateAdapter
        setSpinnerListener(spinner, calendar)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSpinnerListener(spinner: Spinner, calendar: Calendar) = with(binding) {
        spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyBoard(requireActivity())
            }
            false
        }

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            @SuppressLint("SimpleDateFormat")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                clearFocus(etRegisterLessonPrice)
                clearFocus(etRegisterLessonMessage)

                val date = SimpleDateFormat("yyyy-MM-dd")
                date.timeZone = TimeZone.getTimeZone("Asia/Seoul")

                selectedItem = spnRegisterGoalLessonDate.selectedItemPosition

                when (selectedItem) {
                    DEFAULT -> {
                        decideLessonGoalDate(false)
                    }

                    ONE_WEEK -> {
                        decideLessonGoalDate(true)
                        isStartDateInitialized(calendar)
                        calendar.add(Calendar.DATE, 7)
                        calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
                    }

                    ONE_MONTH -> {
                        decideLessonGoalDate(true)
                        isStartDateInitialized(calendar)
                        calendar.add(Calendar.MONTH, 1)
                        calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
                    }

                    TWO_MONTH -> {
                        decideLessonGoalDate(true)
                        isStartDateInitialized(calendar)
                        calendar.add(Calendar.MONTH, 2)
                        calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
                    }

                    THREE_MONTH -> {
                        decideLessonGoalDate(true)
                        isStartDateInitialized(calendar)
                        calendar.add(Calendar.MONTH, 3)
                        calendarToText(LESSON_END_DATE, calendar, tvRegisterGoalLessonDateInfo)
                    }

                    CUSTOM_SETTING -> {
                        registerGoalLessonDate(calendar)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                decideLessonGoalDate(false)
            }
        }
    }

    private fun validateInputForm(editText: EditText, button: AppCompatButton) = with(binding) {
        var result = ""
        val decimalFormat = DecimalFormat("#,###")

        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
                if (!TextUtils.isEmpty(charSequence!!.toString()) && charSequence.toString() != result) {
                    lessonPrice = charSequence.toString().replace(",", "").toInt()
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
                    result = ""
                    editText.setText(result)
                    tvRegisterLessonPriceInfo.apply {
                        text = result
                        visibility = View.INVISIBLE
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    lockButton(button, requireContext())
                } else {
                    unlockButton(button, requireContext())
                }
            }
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
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                clearFocus(etRegisterLessonPrice)
            }
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
            } else {
                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
            }
        }
    }

    private fun clearFocus(editText: EditText) {
        editText.apply {
            clearFocus()
            setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
        }
    }
}