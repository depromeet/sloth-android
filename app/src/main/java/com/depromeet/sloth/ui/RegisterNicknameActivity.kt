package com.depromeet.sloth.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import com.depromeet.sloth.databinding.ActivityRegisterNicknameBinding
import com.depromeet.sloth.ui.base.BaseActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.login.LoginSlothResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.data.network.member.MemberInfoResponse
import com.depromeet.sloth.data.network.member.MemberInfoState
import com.depromeet.sloth.data.network.register.RegisterState
import android.widget.DatePicker

import android.app.DatePickerDialog.OnDateSetListener

import android.app.DatePickerDialog
import com.depromeet.sloth.data.db.PreferenceManager
import java.lang.Exception
import java.util.*


class RegisterNicknameActivity :
    BaseActivity<RegisterViewModel, ActivityRegisterNicknameBinding>() {

    private val pm: PreferenceManager = PreferenceManager(this)

    lateinit var accessToken: String

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel()

    override fun getViewBinding(): ActivityRegisterNicknameBinding =
        ActivityRegisterNicknameBinding.inflate(layoutInflater)


    lateinit var memberId: String

    lateinit var memberName: String

    override fun initViews() = with(binding) {
        tbRegisterNickname.setNavigationOnClickListener { finish() }

        accessToken = pm.getAccessToken().toString()

        focusInputForm(etRegisterNickname, btnRegisterNickname)

        btnRegisterNickname.setOnClickListener {
            val nickname = etRegisterNickname.text.toString()

            if(nickname.isNotEmpty()) {
                mainScope {
                    /*
                    viewModel function 을 async ~await() 로 감싸줘야 it:MemberInfoState<MemberInfoResponse>
                    launch 로 감싸면 it: Job
                     */
                    viewModel.fetchMemberInfo(accessToken).let {
                        when (it) {
                            is MemberInfoState.Success<MemberInfoResponse> -> {
                                Log.d("Success", "${it.data}")

                                memberName = it.data.memberName
                                memberId = it.data.memberId.toString()

                                Log.d("memberId", memberId)
                            }
                            is MemberInfoState.Error ->
                                Log.d("Error", "${it.exception}")
                        }
                    }

                    /*기존의 닉네임과 비교*/
                    if(nickname != memberName) {
                        viewModel.registerNickname(accessToken, nickname).let {
                            when (it) {
                                is RegisterState.Success -> {
                                    Log.d("Register Success", "${it.data}")
                                }

                                is RegisterState.Error ->
                                    Log.d("Register Error", "${it.exception}")
                            }
                        }
                    }
                    else {
                        Toast.makeText(this@RegisterNicknameActivity, "현재 닉네임과 동일합니다.",Toast.LENGTH_SHORT).show()
                    }
                }

                /*
                startActivity(RegisterLesson1Activity.newIntent(this@RegisterNicknameActivity))
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                finish()
                */
            }
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
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}

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
}