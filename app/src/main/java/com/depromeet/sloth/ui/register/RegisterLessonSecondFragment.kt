package com.depromeet.sloth.ui.register

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class RegisterLessonSecondFragment : BaseFragment<FragmentRegisterLessonSecondBinding>() {
    companion object {
        private const val DAY = 86400000L

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

        // if (tvRegisterGoalLessonDateInfo.isVisible)
        if (isLessonGoalDateDecided) {
            tvRegisterGoalLessonDateInfo.visibility = View.VISIBLE
            Log.d("lessonGoalDate", lessonGoalDate)

            tvRegisterGoalLessonDateInfo.text = changeDateFormat(lessonGoalDate)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initViews() = with(binding) {
        if (::goalDateAdapter.isInitialized.not()) {
            initAdapter()
        }

        bindSpinner()

        validateInputForm(etRegisterLessonPrice, btnRegisterLesson)
        focusInputFormOptional(etRegisterLessonMessage)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))

        // 시작일 default 값 오늘
        if (::lessonStartDate.isInitialized.not()) {
            lessonStartDate = LocalDate.now(ZoneId.of("Asia/Seoul")).toString()
            tvRegisterStartLessonDateInfo.text = changeDateFormat(lessonStartDate)
            calendar.time = Date()
            startDay = calendar.timeInMillis
        } else tvRegisterStartLessonDateInfo.text = changeDateFormat(lessonStartDate)

        tvRegisterStartLessonDateInfo.setOnClickListener {
            registerStartLessonDate(calendar)
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
                    0 -> {
                        tvRegisterGoalLessonDateInfo.visibility = View.GONE
                        lockButton(btnRegisterLesson)
                        isLessonGoalDateDecided = false
                    }

                    1, 2, 3, 4 -> {
                        tvRegisterGoalLessonDateInfo.visibility = View.GONE
                        isLessonGoalDateDecided = false
                    }

                    else -> {
                        // 직접선택
                        //if (!tvRegisterGoalLessonDateInfo.isVisible)
                        if (!isLessonGoalDateDecided) {
                            tvRegisterGoalLessonDateInfo.visibility = View.VISIBLE
                            registerGoalLessonDate(calendar)
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                tvRegisterGoalLessonDateInfo.visibility = View.GONE
                lockButton(btnRegisterLesson)
                isLessonGoalDateDecided = false
            }
        }

        tvRegisterGoalLessonDateInfo.setOnClickListener {
            clearFocus(binding.etRegisterLessonPrice)

            registerGoalLessonDate(calendar)
        }

        btnRegisterLesson.setOnClickListener {
            clearFocus(etRegisterLessonPrice)

            when (selectedItem) {
                0 -> {
                    Toast.makeText(requireContext(),
                        "완강 목표일을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                1 -> {
                    if (::startDate.isInitialized) {
                        calendar.time = startDate
                    } else {
                        calendar.time = today
                    }
                    calendar.add(Calendar.DATE, 7)
                    goalDay = calendar.timeInMillis
                    lessonGoalDate = getPickerDateToDash(calendar.time)
                }

                2 -> {
                    if (::startDate.isInitialized) {
                        calendar.time = startDate
                    } else {
                        calendar.time = today
                    }
                    calendar.add(Calendar.MONTH, 1)
                    goalDay = calendar.timeInMillis
                    lessonGoalDate = getPickerDateToDash(calendar.time)
                }

                3 -> {
                    if (::startDate.isInitialized) {
                        calendar.time = startDate
                    } else {
                        calendar.time = today
                    }
                    calendar.add(Calendar.MONTH, 2)
                    goalDay = calendar.timeInMillis
                    lessonGoalDate = getPickerDateToDash(calendar.time)
                }

                4 -> {
                    if (::startDate.isInitialized) {
                        calendar.time = startDate
                    } else {
                        calendar.time = today
                    }
                    calendar.add(Calendar.MONTH, 3)
                    goalDay = calendar.timeInMillis
                    lessonGoalDate = getPickerDateToDash(calendar.time)
                }
            }

            if (startDay!! >= goalDay!!) {
                Toast.makeText(requireContext(),
                    "강의 시작일은 완강 목표일 이전이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etRegisterLessonPrice.text.toString().isEmpty()) {
                Toast.makeText(requireContext(),
                    "강의 금액을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val args = Bundle().apply {
                putString(LESSON_NAME, lessonName)
                putInt(LESSON_COUNT, lessonCount as Int)
                putString(LESSON_CATEGORY_NAME, lessonCategoryName)
                putString(LESSON_SITE_NAME, lessonSiteName)

                putString(LESSON_START_DATE, lessonStartDate)
                putString(LESSON_GOAL_DATE, lessonGoalDate)
                putInt(LESSON_PRICE, etRegisterLessonPrice.text.toString().toInt())
                putString(LESSON_MESSAGE, etRegisterLessonMessage.text.toString())
            }

            Log.d("bundle", "$args")

            // nextFragment
            (activity as RegisterLessonActivity).changeFragment(
                (activity as RegisterLessonActivity).registerLessonCheckFragment, args)
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
                tvRegisterStartLessonDateInfo.text = getPickerDateToDot(calendar.time)

                startDay = calendar.timeInMillis
                lessonStartDate = getPickerDateToDash(calendar.time)
            }
        }
        materialDatePicker.show(childFragmentManager, "calendar")
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
                tvRegisterGoalLessonDateInfo.text =
                    getPickerDateToDot(calendar.time)

                goalDay = calendar.timeInMillis
                lessonGoalDate = getPickerDateToDash(calendar.time)
                isLessonGoalDateDecided = true
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

    private fun validateInputForm(editText: EditText, button: AppCompatButton) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    lockButton(button)
                } else {
                    if (editable.length != 1 && editable[0] == '0') {
                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
                        lockButton(button)
                    } else {
                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                        unlockButton(button)
                    }
                }
            }
        })

        editText.setOnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                if (editText.text.toString().isNotEmpty()) {
                    if (editText.text.length != 1 && editText.text.toString()[0] == '0')
                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
                    else editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                } else {
                    editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
                }
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun unlockButton(button: AppCompatButton) {
        button.isEnabled = true
        button.background = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.bg_register_rounded_button_sloth
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lockButton(button: AppCompatButton) {
        button.isEnabled = false
        button.background = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.bg_register_rounded_button_disabled
        )
    }

    private fun clearFocus(editText: EditText) {
        editText.apply {
            clearFocus()
            setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
        }
    }
}