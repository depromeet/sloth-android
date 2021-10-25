package com.depromeet.sloth.ui

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.animation.LinearInterpolator
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.RegisterModel
import com.depromeet.sloth.databinding.ActivityRegisterLesson2Binding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.adapter.RegisterAdapter
import kotlin.math.ceil

class RegisterLesson2Activity : BaseActivity<RegisterViewModel, ActivityRegisterLesson2Binding>() {

    lateinit var registerAdapter: RegisterAdapter

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel()

    override fun getViewBinding(): ActivityRegisterLesson2Binding =
        ActivityRegisterLesson2Binding.inflate(layoutInflater)

    private var flag = 0

    private val itemList : ArrayList<RegisterModel> = ArrayList()

    companion object {
        fun newIntent(activity: Activity) = Intent(activity, RegisterLesson2Activity::class.java)
    }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }

        initRecyclerView()

        progressbar.progress = 50

        itemList.add(RegisterModel("강의 시작일", "강의 이름을 입력해주세요"))

        registerAdapter.differ.submitList(itemList.toList())

        //이 방법 안될 것 같은데

        registerButton.setOnClickListener {

            if (flag == 0) {

                itemList.add(0, RegisterModel("완강 목표일", "강의 제목을 입력해주세요"))
                Log.d("itemListSize", itemList[0].toString())

                registerAdapter.differ.submitList(itemList.toList())

                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 1) {
                itemList.add(0, RegisterModel("강의 금액", "예)10,000원"))
                Log.d("itemListSize", itemList[0].toString())

                registerAdapter.differ.submitList(itemList.toList())

                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 2) {
                itemList.add(0, RegisterModel("푸시 알람주기", "강의 사이트를 입력해주세요"))
                Log.d("itemListSize", itemList[0].toString())

                registerAdapter.differ.submitList(itemList.toList())

                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 3) {
                itemList.add(0, RegisterModel("각오 한 마디(선택)", "최대 30자 까지 입력 가능합니다."))
                Log.d("itemListSize", itemList[0].toString())

                registerAdapter.differ.submitList(itemList.toList())

                flag += 1

                fillProgressbar(flag, 50)

                return@setOnClickListener
            }

            if (flag == 4) {
                mainScope {
                    viewModel.registerLesson()
                }
            }
        }
    }

    private fun initRecyclerView() {
        registerAdapter = RegisterAdapter()
        binding.recyclerView.apply {
            adapter = registerAdapter
        }
    }

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