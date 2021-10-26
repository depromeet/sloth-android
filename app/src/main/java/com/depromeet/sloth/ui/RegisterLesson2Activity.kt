package com.depromeet.sloth.ui

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.RegisterModel
import com.depromeet.sloth.databinding.ActivityRegisterLesson2Binding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.adapter.RegisterAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*
import kotlin.math.ceil

class RegisterLesson2Activity : BaseActivity<RegisterViewModel, ActivityRegisterLesson2Binding>() {

//    lateinit var registerAdapter: RegisterAdapter

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel()

    override fun getViewBinding(): ActivityRegisterLesson2Binding =
        ActivityRegisterLesson2Binding.inflate(layoutInflater)

    private var flag = 0

//    private val itemList : ArrayList<RegisterModel> = ArrayList()

    companion object {
        fun newIntent(activity: Activity, lessonName: String, totalNumber:Int, categoryId: Int, siteId: Int) = Intent(activity, RegisterLesson2Activity::class.java).apply {
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

    lateinit var lessonName: String
    lateinit var totalNumber: Number
    lateinit var categoryId: Number
    lateinit var siteId: Number
    lateinit var startDate: String
    lateinit var endDate: String
    lateinit var price: Number
    lateinit var alertDays: String
    /*
    message 는 선택사항
     */

    override fun initViews() = with(binding) {

        toolbar.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }

//        initRecyclerView()

        progressbar.progress = 50

//        itemList.add(RegisterModel("강의 시작일", "강의 이름을 입력해주세요"))
////
////        registerAdapter.differ.submitList(itemList.toList())


        startDay.setOnClickListener {

            val materialDateBuilder = MaterialDatePicker.Builder.datePicker()
            val materialDatePicker = materialDateBuilder.build()

            materialDatePicker.show(supportFragmentManager, "calendar")

            materialDatePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

                calendar.time = Date(it)
                binding.startDay.text =
                    "${calendar.get(Calendar.YEAR)}. " + "${calendar.get(Calendar.MONTH) + 1}. " + "${calendar.get(Calendar.DAY_OF_MONTH)}"
            }
        }

        endDay.setOnClickListener {
            val materialDateBuilder = MaterialDatePicker.Builder.datePicker()
            val materialDatePicker = materialDateBuilder.build()

            materialDatePicker.show(supportFragmentManager, "calendar")

            materialDatePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

                calendar.time = Date(it)
                binding.endDay.text =
                    "${calendar.get(Calendar.YEAR)}. " + "${calendar.get(Calendar.MONTH) + 1}. " + "${calendar.get(Calendar.DAY_OF_MONTH)}"
            }
        }

        val animation = AlphaAnimation(0f, 1f)
        animation.duration = 300

        val aniSlide = AnimationUtils.loadAnimation(this@RegisterLesson2Activity, R.anim.slide_down)


        registerButton.setOnClickListener {

            if (flag == 0) {

//                itemList.add(0, RegisterModel("완강 목표일", "강의 제목을 입력해주세요"))
//                Log.d("itemListSize", itemList[0].toString())
//
//                registerAdapter.differ.submitList(itemList.toList())

                endTextView.visibility = View.VISIBLE
                endTextView.startAnimation(aniSlide)

                endDay.visibility = View.VISIBLE
                endDay.startAnimation(aniSlide)

                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 1) {
//                itemList.add(0, RegisterModel("강의 금액", "예)10,000원"))
//                Log.d("itemListSize", itemList[0].toString())
//
//                registerAdapter.differ.submitList(itemList.toList())

                priceTextView.visibility = View.VISIBLE
                priceTextView.startAnimation(aniSlide)

                priceEditText.visibility = View.VISIBLE
                priceEditText.startAnimation(aniSlide)

                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 2) {
//                itemList.add(0, RegisterModel("푸시 알람주기", "강의 사이트를 입력해주세요"))
//                Log.d("itemListSize", itemList[0].toString())
//
//                registerAdapter.differ.submitList(itemList.toList())

                alertTextView.visibility = View.VISIBLE
                alertTextView.startAnimation(aniSlide)

                chipGroupLayout.visibility = View.VISIBLE
                chipGroupLayout.startAnimation(aniSlide)

                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 3) {
//                itemList.add(0, RegisterModel("각오 한 마디(선택)", "최대 30자 까지 입력 가능합니다."))
//                Log.d("itemListSize", itemList[0].toString())
//
//                registerAdapter.differ.submitList(itemList.toList())

                messageTextView.visibility = View.VISIBLE
                messageTextView.startAnimation(aniSlide)

                messageEditText.visibility = View.VISIBLE
                messageEditText.startAnimation(aniSlide)



                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 4) {
//                mainScope {
//                    viewModel.registerLesson(lessonModel)
//                }
            }
        }
    }

//    private fun initRecyclerView() {
//        registerAdapter = RegisterAdapter()
//        binding.recyclerView.apply {
//            adapter = registerAdapter
//        }
//    }

    private fun fillProgressbar(count: Int, default: Int) {
        val animation = ObjectAnimator.ofInt(
            binding.progressbar,
            "progress",
            default + ceil((count-1) * 12.5).toInt(),
            default + ceil(count * 12.5).toInt())

        animation.duration = 300
        animation.interpolator = LinearInterpolator()
        animation.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
}