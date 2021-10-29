package com.depromeet.sloth.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.depromeet.sloth.R
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.model.LessonModel
import com.depromeet.sloth.data.model.RegisterModel
import com.depromeet.sloth.data.network.register.RegisterState
import com.depromeet.sloth.databinding.ActivityRegisterLesson2Binding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.adapter.RegisterAdapter
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.lang.Exception
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.ceil

class RegisterLesson2Activity : BaseActivity<RegisterViewModel, ActivityRegisterLesson2Binding>() {

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel()

    override fun getViewBinding(): ActivityRegisterLesson2Binding =
        ActivityRegisterLesson2Binding.inflate(layoutInflater)

    private val pm = PreferenceManager(this)

    private var flag = 0

    companion object {

        fun newIntent(
            activity: Activity,
            lessonName: String,
            totalNumber: Int,
            categoryId: Int,
            siteId: Int
        ) = Intent(activity, RegisterLesson2Activity::class.java).apply {
            putExtra(LESSON_NAME, lessonName)
            putExtra(TOTAL_NUMBER, totalNumber)
            putExtra(CATEGORY_ID, categoryId)
            putExtra(SITE_ID, siteId)
        }

        private const val LESSON_NAME = "lessonName"
        private const val TOTAL_NUMBER = "totalNumber"
        private const val CATEGORY_ID = "categoryId"
        private const val SITE_ID = "siteId"
    }

    lateinit var accessToken: String

    private var alertDays: String? = null
    lateinit var categoryId: Number
    lateinit var endDate: String
    lateinit var lessonName: String
    lateinit var price: Number
    lateinit var message: String
    lateinit var siteId: Number
    lateinit var startDate: String
    lateinit var totalNumber: Number

    private var startDay: Date? = null
    private var endDay: Date? = null

    /*
    alertDays 는 일단은 null 로 세팅
    message 는 선택사항
     */

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun initViews() = with(binding) {

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        accessToken = pm.getAccessToken().toString()

        intent.apply {
            categoryId = getIntExtra(CATEGORY_ID, -1)
            lessonName = getStringExtra(LESSON_NAME).toString()
            siteId = getIntExtra(SITE_ID, -1)
            totalNumber = getIntExtra(TOTAL_NUMBER, 0)

        }

        progressbar.progress = 50

        /*val animation = AlphaAnimation(0f, 1f)
        animation.duration = 300*/

        val aniSlide = AnimationUtils.loadAnimation(this@RegisterLesson2Activity, R.anim.slide_down)

        if (flag == 0) {
            startDateTextView.setOnClickListener {

                var calendar: Calendar = Calendar.getInstance()

                var datePickerDialog = DatePickerDialog(
                    this@RegisterLesson2Activity, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    { view, year, monthOfYear, dayOfMonth ->
                        try {
                            val month =
                                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
                            val date =
                                if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                            val pickedDate = "$year-$month-$date"

                            startDay = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(
                                year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                            )
                            Log.d("startDay: ", startDay.toString())

                            startDate = pickedDate
                            startDateTextView.text = pickedDate
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                )

                datePickerDialog.datePicker.calendarViewShown = false
                datePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                datePickerDialog.show()

                startAnimation(aniSlide, endDayTextView, endDateTextView)

                flag += 1

                fillProgressbar(flag, 50)

                hideKeyboard()

                if (flag == 1) {
                    endDateTextView.setOnClickListener {

                        calendar = Calendar.getInstance()

                        datePickerDialog = DatePickerDialog(
                            this@RegisterLesson2Activity,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            { _, year, monthOfYear, dayOfMonth ->
                                try {
                                    val month =
                                        if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
                                    val date =
                                        if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                                    val pickedDate = "$year-$month-$date"

                                    endDay =
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(
                                            year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                                        )
                                    Log.d("endDate: ", endDay.toString())

                                    endDate = pickedDate
                                    endDateTextView.text = pickedDate

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                        )
                        datePickerDialog.datePicker.minDate = startDay!!.time
                        datePickerDialog.datePicker.calendarViewShown = false
                        datePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                        datePickerDialog.show()

                        startAnimation(aniSlide, priceTextView, priceEditText)

                        flag += 1

                        fillProgressbar(flag, 50)

                        hideKeyboard()

                        if (flag == 2) {
                            lockButton(registerButton)

                            focusInputForm(priceEditText, registerButton)

                            registerButton.setOnClickListener {

                                price = priceEditText.text.toString().toInt()

                                val df = DecimalFormat("#,###")
                                val changedPriceFormat = df.format(price)

                                priceEditText.setText("${changedPriceFormat}원")

                                startAnimation(aniSlide, messageTextView, messageEditText)

                                flag += 1

                                fillProgressbar(flag, 50)

                                hideKeyboard()

                                if (flag == 3) {
                                    registerButton.setOnClickListener {

                                        message = messageEditText.text.toString()

                                        val lessonInfo = LessonModel(
                                            alertDays = alertDays,
                                            categoryId = categoryId.toInt(),
                                            endDate = endDate,
                                            lessonName = lessonName,
                                            message = message,
                                            price = price.toInt(),
                                            siteId = siteId.toInt(),
                                            startDate = startDate,
                                            totalNumber = totalNumber.toInt()
                                        )

                                        Log.d("lessonInfo: ", "$lessonInfo")

                                        mainScope {
                                            viewModel.registerLesson(accessToken, lessonInfo).let {
                                                when (it) {
                                                    is RegisterState.Success -> {
                                                        Log.d("Register Success", "${it.data}")
                                                    }

                                                    is RegisterState.Error ->
                                                        Log.d("Register Error", "${it.exception}")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun fillProgressbar(count: Int, default: Int) {
        val animation = ObjectAnimator.ofInt(
            binding.progressbar,
            "progress",
            default + ceil((count - 1) * 12.5).toInt(),
            default + ceil(count * 12.5).toInt()
        )

        animation.duration = 300
        animation.interpolator = LinearInterpolator()
        animation.start()
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun unlockButton(button: Button) {
        button.isEnabled = true
        button.setBackgroundColor(
            resources.getColor(
                R.color.primary_500,
                theme
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lockButton(button: Button) {
        button.isEnabled = false
        button.setBackgroundColor(
            resources.getColor(
                R.color.gray_300,
                theme
            )
        )
    }

    private fun focusInputForm(editText: EditText, button: Button) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    lockButton(button)
                } else {
                    unlockButton(button)
                }
            }
        })
    }

    private fun startAnimation(animation: Animation, textView: TextView, view: View) {
        textView.visibility = View.VISIBLE
        textView.startAnimation(animation)

        view.visibility = View.VISIBLE
        view.startAnimation(animation)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}