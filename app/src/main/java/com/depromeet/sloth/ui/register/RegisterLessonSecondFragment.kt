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
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_COUNT
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_NAME
import com.depromeet.sloth.ui.register.RegisterLessonFirstFragment.Companion.LESSON_SITE_NAME
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RegisterLessonSecondFragment : BaseFragment<FragmentRegisterLessonSecondBinding>() {
    companion object {
        private const val DAY = 86400000L

        private const val DEFAULT = 0
        private const val ONE_WEEK = 1
        private const val ONE_MONTH = 2
        private const val TWO_MONTH = 3
        private const val THREE_MONTH = 4
        private const val CUSTOM_SETTING = 5

        const val LESSON_START_DATE = "lessonStartDate"
        const val LESSON_GOAL_DATE = "lessonGoalDate"
        const val LESSON_PRICE = "lessonPrice"
        const val LESSON_PUSH_NOTI_CYCLE = "lessonPushNotiCycle"
        const val LESSON_MESSAGE = "lessonMessage"
    }

    lateinit var lessonName: String
    lateinit var lessonCount: Number
    lateinit var lessonCategoryName: String
    lateinit var lessonSiteName: String
    lateinit var lessonPrice: Number

    private var startDay: Long? = null
    private var goalDay: Long? = null

    lateinit var startDate: Date

    lateinit var lessonStartDate: String
    lateinit var lessonGoalDate: String

    private var isLessonGoalDateDecided = false

    lateinit var selectedItem: Number

    lateinit var goalDateAdapter: ArrayAdapter<String>

    private val today = Date()

    override fun getViewBinding(): FragmentRegisterLessonSecondBinding =
        FragmentRegisterLessonSecondBinding.inflate(layoutInflater)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            lessonName = getString(LESSON_NAME).toString()
            lessonCount = getInt(LESSON_COUNT)
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
            tvRegisterGoalLessonDateInfo.text = changeDateFormat(lessonGoalDate)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        if (::goalDateAdapter.isInitialized.not()) {
            initAdapter()
        }

        bindSpinner()

        (activity as RegisterLessonActivity).lockButton(btnRegisterLesson)

        validateInputForm(etRegisterLessonPrice, btnRegisterLesson)
        focusInputFormOptional(etRegisterLessonMessage)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

        if (::lessonStartDate.isInitialized.not()) {
            calendar.time = today
            calendarToText(LESSON_START_DATE, calendar, tvRegisterStartLessonDateInfo)
        } else {
            tvRegisterStartLessonDateInfo.text = changeDateFormat(lessonStartDate)
        }

        spnRegisterGoalLessonDate.setSelection(0, false)

        spnRegisterGoalLessonDate.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            @SuppressLint("SimpleDateFormat")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                clearFocus(binding.etRegisterLessonPrice)

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
                        calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
                    }

                    ONE_MONTH -> {
                        decideLessonGoalDate(true)
                        isStartDateInitialized(calendar)
                        calendar.add(Calendar.MONTH, 1)
                        calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
                    }

                    TWO_MONTH -> {
                        decideLessonGoalDate(true)
                        isStartDateInitialized(calendar)
                        calendar.add(Calendar.MONTH, 2)
                        calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
                    }

                    THREE_MONTH -> {
                        decideLessonGoalDate(true)
                        isStartDateInitialized(calendar)
                        calendar.add(Calendar.MONTH, 3)
                        calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
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
                    "완강 목표일을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etRegisterLessonPrice.text.toString().isEmpty()) {
                Toast.makeText(requireContext(),
                    "강의 금액을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (startDay!! >= goalDay!!) {
                Toast.makeText(requireContext(),
                    "강의 시작일은 완강 목표일 이전이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val args = Bundle().apply {
                putString(LESSON_NAME, lessonName)
                putInt(LESSON_COUNT, lessonCount as Int)
                putString(LESSON_CATEGORY_NAME, lessonCategoryName)
                putString(LESSON_SITE_NAME, lessonSiteName)
                putString(LESSON_START_DATE, lessonStartDate)
                putString(LESSON_GOAL_DATE, lessonGoalDate)
                putInt(LESSON_PRICE, lessonPrice.toInt())
                putString(LESSON_MESSAGE, etRegisterLessonMessage.text.toString())
            }

            (activity as RegisterLessonActivity).changeFragment(
                (activity as RegisterLessonActivity).registerLessonCheckFragment, args)
        }
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
            (activity as RegisterLessonActivity).unlockButton(btnRegisterLesson)
            isLessonGoalDateDecided = true
        } else {
            tvRegisterGoalLessonDateInfo.visibility = View.GONE
            (activity as RegisterLessonActivity).lockButton(btnRegisterLesson)
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
                goalDay = calendar.timeInMillis
                lessonGoalDate = getPickerDateToDash(calendar.time)
            }
            if (textView != null) {
                textView.text = getPickerDateToDot(calendar.time)
            }
        }

    private fun updateGoalLessonDate(calendar: Calendar) = with(binding) {
        when (selectedItem) {
            ONE_WEEK -> {
                calendar.add(Calendar.DATE, 7)
                calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
            }

            ONE_MONTH -> {
                calendar.add(Calendar.MONTH, 1)
                calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
            }

            TWO_MONTH -> {
                calendar.add(Calendar.MONTH, 2)
                calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
            }

            THREE_MONTH -> {
                calendar.add(Calendar.MONTH, 3)
                calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
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
                calendarToText(LESSON_GOAL_DATE, calendar, tvRegisterGoalLessonDateInfo)
                decideLessonGoalDate(true)
            }
        }
        materialDatePicker.show(childFragmentManager, "calendar")
    }

    private fun initAdapter() {
        goalDateAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            resources.getStringArray(R.array.lesson_goal_date_array)
        ).apply {
            setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        }
    }

    private fun bindSpinner() {
        binding.spnRegisterGoalLessonDate.adapter
    }

    private fun getPickerDateToDash(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        return formatter.format(date)
    }

    private fun getPickerDateToDot(date: Date): String {
        val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        return formatter.format(date)
    }

    private fun changeDateFormat(date: String): String {
        val dateArr = date.split("-")

        val yearOfDate = dateArr[0]
        val monthOfDate = dateArr[1]
        val dayOfDate = dateArr[2]

        return "${yearOfDate}.${monthOfDate}.$dayOfDate"
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
                    tvRegisterLessonPriceInfo.apply {
                        text = getString(R.string.input_lesson_price, result)
                        visibility = View.VISIBLE
                    }

                    editText.setSelection(result.length)
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
                    (activity as RegisterLessonActivity).lockButton(button)
                } else {
                    (activity as RegisterLessonActivity).unlockButton(button)
                }
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

    private fun focusInputFormOptional(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                clearFocus(binding.etRegisterLessonPrice)
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