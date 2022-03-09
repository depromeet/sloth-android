//package com.depromeet.sloth.ui.register
//
//import android.animation.ObjectAnimator
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.view.View
//import android.view.animation.Animation
//import android.view.animation.AnimationUtils
//import android.view.animation.LinearInterpolator
//import android.view.inputmethod.InputMethodManager
//import android.widget.*
//import androidx.activity.viewModels
//import androidx.annotation.RequiresApi
//import androidx.appcompat.content.res.AppCompatResources
//import androidx.appcompat.widget.AppCompatButton
//import androidx.core.view.isVisible
//import com.depromeet.sloth.R
//import com.depromeet.sloth.data.PreferenceManager
//import com.depromeet.sloth.data.network.lesson.LessonRegisterRequest
//import com.depromeet.sloth.data.network.lesson.LessonState
//import com.depromeet.sloth.databinding.ActivityRegisterLessonSecondBinding
//import com.depromeet.sloth.ui.DialogState
//import com.depromeet.sloth.ui.SlothDialog
//import com.depromeet.sloth.ui.base.BaseActivity
//import com.depromeet.sloth.ui.login.LoginActivity
//import com.google.android.material.datepicker.CalendarConstraints
//import com.google.android.material.datepicker.DateValidatorPointForward
//import com.google.android.material.datepicker.MaterialDatePicker
//import dagger.hilt.android.AndroidEntryPoint
//import java.text.SimpleDateFormat
//import java.util.*
//import javax.inject.Inject
//import kotlin.math.ceil
//
//@AndroidEntryPoint
//class RegisterLessonSecondActivity : BaseActivity<ActivityRegisterLessonSecondBinding>() {
//    companion object {
//        fun newIntent(
//            activity: Activity,
//            lessonName: String,
//            totalNumber: Int,
//            categoryId: Int,
//            siteId: Int,
//        ) = Intent(activity, RegisterLessonSecondActivity::class.java).apply {
//            putExtra(LESSON_NAME, lessonName)
//            putExtra(TOTAL_NUMBER, totalNumber)
//            putExtra(CATEGORY_ID, categoryId)
//            putExtra(SITE_ID, siteId)
//        }
//
//        private const val LESSON_NAME = "lessonName"
//        private const val TOTAL_NUMBER = "totalNumber"
//        private const val CATEGORY_ID = "categoryId"
//        private const val SITE_ID = "siteId"
//        private const val DAY = 86400000L
//    }
//
//    @Inject
//    lateinit var preferenceManager: PreferenceManager
//    private val viewModel: RegisterViewModel by viewModels()
//
//    override fun getViewBinding(): ActivityRegisterLessonSecondBinding =
//        ActivityRegisterLessonSecondBinding.inflate(layoutInflater)
//
//    private var flag = 0
//
//    lateinit var accessToken: String
//    lateinit var refreshToken: String
//
//    /*
//    alertDays 는 일단은 null 로 세팅
//    message 는 선택사항
//     */
//
//    private var alertDays: String? = null
//    lateinit var categoryId: Number
//    lateinit var endDate: String
//    lateinit var lessonName: String
//    lateinit var message: String
//    lateinit var siteId: Number
//    lateinit var startDate: String
//    lateinit var totalNumber: Number
//    private var startDay: Long? = null
//    private var endDay: Long? = null
//
//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        accessToken = preferenceManager.getAccessToken()
//        refreshToken = preferenceManager.getRefreshToken()
//
//        initViews()
//    }
//
//    @SuppressLint("SetTextI18n")
//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun initViews() = with(binding) {
//        tbRegisterLesson.setNavigationOnClickListener {
//            onBackPressed()
//        }
//
//        intent.apply {
//            categoryId = getIntExtra(CATEGORY_ID, -1)
//            lessonName = getStringExtra(LESSON_NAME).toString()
//            siteId = getIntExtra(SITE_ID, -1)
//            totalNumber = getIntExtra(TOTAL_NUMBER, 0)
//        }
//
//        pbRegisterLesson.progress = 50
//
//        val aniSlide =
//            AnimationUtils.loadAnimation(this@RegisterLessonSecondActivity, R.anim.slide_down)
//
//        if (flag == 0) {
//            lockButton(btnRegisterLesson)
//
//            tvRegisterStartLessonDateInfo.setOnClickListener {
//
//                var materialDateBuilder = MaterialDatePicker.Builder.datePicker().apply {
//                    setTitleText(getString(R.string.lesson_start_date))
//                }
//
//                var materialDatePicker = materialDateBuilder.build().apply {
//                    show(supportFragmentManager, "calendar")
//                }
//
//                materialDatePicker.addOnPositiveButtonClickListener {
//                    var calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
//
//                    calendar.time = Date(it)
//
//                    var pickerDate = getPickerTime(calendar.time)
//
//                    binding.tvRegisterStartLessonDateInfo.text = pickerDate
//
//                    startDay = calendar.timeInMillis
//
//                    startDate = pickerDate
//
//                    if (!tvRegisterEndLessonDate.isVisible) {
//
//                        flag += 1
//                        fillProgressbar(flag, 50)
//
//                        startAnimation(
//                            aniSlide,
//                            tvRegisterEndLessonDate,
//                            tvRegisterEndLessonDateInfo
//                        )
//                    }
//
//                    if (flag == 1) {
//                        tvRegisterEndLessonDateInfo.setOnClickListener {
//
//                            val constraintsBuilder =
//                                CalendarConstraints.Builder()
//                                    .setValidator(DateValidatorPointForward.from(startDay!! + DAY))
//
//                            materialDateBuilder = MaterialDatePicker.Builder.datePicker().apply{
//                                setCalendarConstraints(constraintsBuilder.build())
//                                setTitleText(getString(R.string.lesson_finish_date))
//                            }
//
//                            materialDatePicker = materialDateBuilder.build().apply{
//                                show(supportFragmentManager, "calendar")
//                            }
//
//                            materialDatePicker.addOnPositiveButtonClickListener {
//                                calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
//
//                                calendar.time = Date(it)
//
//                                pickerDate = getPickerTime(calendar.time)
//
//                                binding.tvRegisterEndLessonDateInfo.text = pickerDate
//
//                                endDay = calendar.timeInMillis
//
//                                endDate = pickerDate
//
//                                if (!tvRegisterLessonPrice.isVisible) {
//                                    flag += 1
//                                    fillProgressbar(flag, 50)
//
//                                    startAnimation(
//                                        aniSlide,
//                                        tvRegisterLessonPrice,
//                                        etRegisterLessonPriceInfo
//                                    )
//                                }
//
//                                if (flag == 2) {
//                                    validateInputForm(etRegisterLessonPriceInfo, btnRegisterLesson)
//
//                                    btnRegisterLesson.setOnClickListener {
//
//                                        //price = priceEditText.text.toString().toInt()
//
//                                        /*val df = DecimalFormat("#,###")
//                                        val changedPriceFormat = df.format(price)
//
//                                        priceEditText.setText("${changedPriceFormat}원")*/
//
//                                        startAnimation(
//                                            aniSlide,
//                                            tvRegisterLessonMessage,
//                                            etRegisterLessonMessageInfo
//                                        )
//
//                                        if (flag < 3) {
//
//                                            flag += 1
//
//                                            fillProgressbar(flag, 50)
//
//                                            clearFocus(etRegisterLessonPriceInfo)
//                                        }
//
//                                        if (flag == 3) {
//                                            focusInputFormOptional(etRegisterLessonMessageInfo)
//
//                                            btnRegisterLesson.setOnClickListener {
//
//                                                if (startDay!! >= endDay!!) {
//                                                    Toast.makeText(
//                                                        this@RegisterLessonSecondActivity,
//                                                        "강의 시작일은 완강 목표일 이전이어야 해요.",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//
//                                                    return@setOnClickListener
//                                                }
//                                                else {
//                                                    message =
//                                                        etRegisterLessonMessageInfo.text.toString()
//
//                                                    mainScope {
//                                                        viewModel.registerLesson(
//                                                            accessToken,
//                                                            LessonRegisterRequest(
//                                                                alertDays = alertDays,
//                                                                categoryId = categoryId.toInt(),
//                                                                endDate = endDate,
//                                                                lessonName = lessonName,
//                                                                message = message,
//                                                                price = etRegisterLessonPriceInfo.text.toString()
//                                                                    .toInt(),
//                                                                siteId = siteId.toInt(),
//                                                                startDate = startDate,
//                                                                totalNumber = totalNumber.toInt()
//                                                            )
//                                                        ).let {
//                                                            when (it) {
//                                                                is LessonState.Success -> {
//                                                                    Log.d(
//                                                                        "Register Success",
//                                                                        "${it.data}"
//                                                                    )
//                                                                    Toast.makeText(
//                                                                        this@RegisterLessonSecondActivity,
//                                                                        "강의가 등록되었어요.",
//                                                                        Toast.LENGTH_SHORT
//                                                                    ).show()
//
//                                                                    setResult(
//                                                                        RESULT_OK,
//                                                                        Intent(
//                                                                            this@RegisterLessonSecondActivity,
//                                                                            RegisterLessonFirstActivity::class.java
//                                                                        )
//                                                                    )
//                                                                    if (!isFinishing) finish()
//                                                                }
//
//                                                                is LessonState.Unauthorized -> {
//                                                                    val dlg = SlothDialog(
//                                                                        this@RegisterLessonSecondActivity,
//                                                                        DialogState.FORBIDDEN)
//                                                                    dlg.onItemClickListener =
//                                                                        object :
//                                                                            SlothDialog.OnItemClickedListener {
//                                                                            override fun onItemClicked() {
//                                                                                preferenceManager.removeAuthToken()
//                                                                                startActivity(LoginActivity.newIntent(this@RegisterLessonSecondActivity))
//                                                                            }
//                                                                        }
//                                                                    dlg.start()
//                                                                }
//
//                                                                is LessonState.Error -> {
//                                                                    Log.d(
//                                                                        "Register Error",
//                                                                        "${it.exception}"
//                                                                    )
//                                                                    Toast.makeText(
//                                                                        this@RegisterLessonSecondActivity,
//                                                                        "강의 등록을 실패했어요.",
//                                                                        Toast.LENGTH_SHORT
//                                                                    ).show()
//                                                                }
//                                                                else -> Unit
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    private fun fillProgressbar(count: Int, default: Int) {
//        val animation = ObjectAnimator.ofInt(
//            binding.pbRegisterLesson,
//            "progress",
//            default + ceil((count - 1) * 16.667).toInt(),
//            default + ceil(count * 16.667).toInt()
//        )
//        animation.apply {
//            duration = 300
//            interpolator = LinearInterpolator()
//            start()
//        }
//    }
//
//    private fun hideKeyboard() {
//        val view = this.currentFocus
//        if (view != null) {
//            val imm: InputMethodManager =
//                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view.windowToken, 0)
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun unlockButton(button: AppCompatButton) {
//        button.isEnabled = true
//        button.background = AppCompatResources.getDrawable(
//            this,
//            R.drawable.bg_login_policy_rounded_sloth
//        )
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun lockButton(button: AppCompatButton) {
//        button.isEnabled = false
//        button.background = AppCompatResources.getDrawable(
//            this,
//            R.drawable.bg_login_policy_rounded_gray
//        )
//    }
//
//    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
//        editText.addTextChangedListener(object : TextWatcher {
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//            }
//
//            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//
//            }
//
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun afterTextChanged(editable: Editable?) {
//                if (editable.isNullOrEmpty()) {
//                    lockButton(button)
//                } else {
//                    unlockButton(button)
//                }
//            }
//        })
//
//        editText.setOnFocusChangeListener { _, gainFocus ->
//            if (gainFocus) {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
//            } else {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
//            }
//        }
//    }
//
//    private fun focusInputFormOptional(editText: EditText) {
//        editText.setOnFocusChangeListener { _, gainFocus ->
//            if (gainFocus) {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
//            } else {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
//            }
//        }
//    }
//
//    private fun validateInputForm(editText: EditText, button: AppCompatButton) {
//        editText.setOnFocusChangeListener { _, gainFocus ->
//            if (gainFocus) {
//                if (editText.text.toString().isNotEmpty()) {
//                    if(editText.text.length != 1 && editText.text.toString()[0] == '0')
//                    editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
//                } else {
//                    editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
//                }
//            } else {
//                editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_gray)
//            }
//        }
//
//        editText.addTextChangedListener(object : TextWatcher {
//
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//            }
//
//            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
//
//            }
//
//            @RequiresApi(Build.VERSION_CODES.M)
//            override fun afterTextChanged(editable: Editable?) {
//                if (editable.isNullOrEmpty()) {
//                    lockButton(button)
//                } else {
//                    if (editable.length != 1 && editable[0] == '0') {
//                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_error)
//                        lockButton(button)
//                    } else {
//                        editText.setBackgroundResource(R.drawable.bg_register_rounded_edit_text_sloth)
//                        unlockButton(button)
//                    }
//                }
//            }
//        })
//    }
//
//    private fun startAnimation(animation: Animation, textView: TextView, view: View) {
//        textView.visibility = View.VISIBLE
//        textView.startAnimation(animation)
//
//        view.visibility = View.VISIBLE
//        view.startAnimation(animation)
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
//    }
//
//    private fun getPickerTime(date: Date): String {
//        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
//        return formatter.format(date)
//    }
//
//    private fun clearFocus(editText: EditText) {
//        editText.clearFocus()
//        hideKeyboard()
//    }
//}