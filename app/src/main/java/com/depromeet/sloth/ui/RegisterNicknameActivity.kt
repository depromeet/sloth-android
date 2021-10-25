package com.depromeet.sloth.ui

import android.app.Activity
import android.content.Intent
import com.depromeet.sloth.databinding.ActivityRegisterNicknameBinding
import com.depromeet.sloth.ui.base.BaseActivity
import android.text.Editable
import android.text.TextWatcher
import com.depromeet.sloth.R


class RegisterNicknameActivity :
    BaseActivity<RegisterViewModel, ActivityRegisterNicknameBinding>() {

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel()

    override fun getViewBinding(): ActivityRegisterNicknameBinding =
        ActivityRegisterNicknameBinding.inflate(layoutInflater)

    companion object {
        fun newIntent(activity: Activity) = Intent(activity, RegisterNicknameActivity::class.java)
    }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

            }
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    registerButton.setBackgroundColor(resources.getColor(R.color.gray_300, theme))
                } else {
                    registerButton.setBackgroundColor(resources.getColor(R.color.primary_500, theme))
                }
            }
        })

        registerButton.setOnClickListener {
            val nickname = inputEditText.text.toString()

            if(nickname.isNotEmpty()) {
                mainScope {
                    viewModel.registerNickname(nickname)
                }

                startActivity(RegisterLesson1Activity.newIntent(this@RegisterNicknameActivity))
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                finish()
            }
        }
    }
}