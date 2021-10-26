package com.depromeet.sloth.ui

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.RegisterModel
import com.depromeet.sloth.databinding.ActivityRegisterLesson1Binding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.adapter.RegisterAdapter
import kotlin.math.ceil

class RegisterLesson1Activity : BaseActivity<RegisterViewModel, ActivityRegisterLesson1Binding>() {

//    lateinit var registerAdapter: RegisterAdapter

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel()

    override fun getViewBinding(): ActivityRegisterLesson1Binding =
        ActivityRegisterLesson1Binding.inflate(layoutInflater)

    private var flag = 0

//    private val itemList : ArrayList<RegisterModel> = ArrayList()

    lateinit var lessonName: String
    lateinit var totalNumber: Number
    lateinit var categoryId: Number
    lateinit var siteId: Number

    companion object {
        fun newIntent(accessToken: String, activity: Activity) = Intent(activity, RegisterLesson1Activity::class.java).apply{
            putExtra(ACCESS_TOKEN, accessToken)
        }
        const val ACCESS_TOKEN = "accessToken"
    }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
//        initRecyclerView()

//        itemList.add(RegisterModel("강의 이름", "강의 이름을 입력해주세요"))

//        registerAdapter.differ.submitList(itemList.toList())

        val animation = AlphaAnimation(0f, 1f)
        animation.duration = 300

        val aniSlide = AnimationUtils.loadAnimation(this@RegisterLesson1Activity, R.anim.slide_down)


        registerButton.setOnClickListener {

            if (flag == 0) {

//                itemList.add(0, RegisterModel("강의 제목", "강의 제목을 입력해주세요"))
//                Log.d("itemListSize", itemList[0].toString())

//                registerAdapter.differ.submitList(itemList.toList())


                countTextView.visibility = View.VISIBLE
                countTextView.startAnimation(aniSlide)


                countEditText.visibility = View.VISIBLE
                countEditText.startAnimation(aniSlide)

//                nameTextView.startAnimation(aniSlide)
//                nameEditText.startAnimation(aniSlide)

                flag += 1

                fillProgressbar(flag, 0)

                return@setOnClickListener

            }

            if (flag == 1) {
//                itemList.add(0, RegisterModel("강의 개수", "강의 개수를 입력해주세요"))
//                Log.d("itemListSize", itemList[0].toString())

//                registerAdapter.differ.submitList(itemList.toList())

                categoryTextView.visibility = View.VISIBLE
                categoryTextView.startAnimation(aniSlide)

                categorySpinner.visibility = View.VISIBLE
                categorySpinner.startAnimation(aniSlide)

//                countTextView.animation = animation
//                countEditText.animation = animation
//                nameTextView.animation = animation
//                nameEditText.animation = animation

                flag += 1

                fillProgressbar(flag, 0)

                return@setOnClickListener
            }

            if (flag == 2) {
//                itemList.add(0, RegisterModel("강의 사이트", "강의 사이트를 입력해주세요"))
//                Log.d("itemListSize", itemList[0].toString())

//                registerAdapter.differ.submitList(itemList.toList())

                siteTextView.visibility = View.VISIBLE
                siteTextView.startAnimation(aniSlide)

                siteSpinner.visibility = View.VISIBLE
                siteSpinner.startAnimation(aniSlide)

//                categoryTextView.animation = animation
//                categorySpinner.animation = animation
//                countTextView.animation = animation
//                countEditText.animation = animation
//                nameTextView.animation = animation
//                nameEditText.animation = animation

                flag += 1

                fillProgressbar(flag, 0)

                return@setOnClickListener
            }

            if (flag == 3) {

                flag += 1

                fillProgressbar(flag, 0)

                /*
                startActivity(RegisterLesson2Activity.newIntent(this@RegisterLesson1Activity))
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                */
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
}