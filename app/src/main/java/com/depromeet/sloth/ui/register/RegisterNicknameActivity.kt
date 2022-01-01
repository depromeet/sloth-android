package com.depromeet.sloth.ui.register

import android.os.Build
import com.depromeet.sloth.databinding.ActivityRegisterNicknameBinding
import com.depromeet.sloth.ui.base.BaseActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.member.MemberState
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import java.util.*


// 현재 사용하지 않는 화면
class RegisterNicknameActivity : BaseActivity<RegisterViewModel, ActivityRegisterNicknameBinding>() {
    private val preferenceManager: PreferenceManager = PreferenceManager(this)

    lateinit var accessToken: String

    override val viewModel: RegisterViewModel
        get() = RegisterViewModel(preferenceManager)

    override fun getViewBinding(): ActivityRegisterNicknameBinding =
        ActivityRegisterNicknameBinding.inflate(layoutInflater)

    lateinit var memberId: String

    lateinit var memberName: String

    lateinit var memberUpdateInfoRequest: MemberUpdateInfoRequest

    override fun initViews() = with(binding) {
        tbRegisterNickname.setNavigationOnClickListener { finish() }

        accessToken = preferenceManager.getAccessToken().toString()

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
                            is MemberState.Success<com.depromeet.sloth.data.network.member.MemberInfoResponse> -> {
                                Log.d("Success", "${it.data}")

                                memberName = it.data.memberName
                                memberId = it.data.memberId.toString()

                                Log.d("memberId", memberId)
                            }
                            is MemberState.Error ->
                                Log.d("Error", "${it.exception}")

                            else -> Unit
                        }
                    }

                    /*기존의 닉네임과 비교*/
                    if(nickname != memberName) {
                        memberUpdateInfoRequest = MemberUpdateInfoRequest(
                            memberName = nickname
                        )

                        viewModel.updateMemberInfo(accessToken, memberUpdateInfoRequest).let {
                            when (it) {
                                is MemberState.Success -> {
                                    Log.d("Register Success", "${it.data}")
                                }

                                is MemberState.Error ->
                                    Log.d("Register Error", "${it.exception}")
                                else -> Unit
                            }
                        }
                    }
                    else {
                        Toast.makeText(this@RegisterNicknameActivity, "현재 닉네임과 동일합니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun unlockButton(button: AppCompatButton) {
        button.isEnabled = true
        button.background = AppCompatResources.getDrawable(
            this,
            R.drawable.bg_login_policy_rounded_sloth
        )

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lockButton(button: AppCompatButton) {
        button.isEnabled = false
        button.background = AppCompatResources.getDrawable(
            this,
            R.drawable.bg_login_policy_rounded_gray
        )
    }

    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

            }

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