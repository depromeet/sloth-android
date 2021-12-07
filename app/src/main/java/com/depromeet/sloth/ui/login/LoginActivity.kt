package com.depromeet.sloth.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.depromeet.sloth.databinding.ActivityLoginBinding
import android.widget.Button
import com.depromeet.sloth.R
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.detail.LessonDetailActivity
import com.depromeet.sloth.ui.home.HomeActivity

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override val viewModel: LoginViewModel = LoginViewModel()

    override fun getViewBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    companion object {
        fun newIntent(activity: Activity) =
            Intent(activity, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        findViewById<Button>(R.id.btn_login_start).setOnClickListener {
            openLoginBottomSheet()
        }
    }

    private var loginBottomSheet: LoginBottomSheetFragment? = null
    private var registerBottomSheet: RegisterBottomSheetFragment? = null

    private fun openLoginBottomSheet() {
        loginBottomSheet?.run {
            val loginListener = object : LoginListener {
                override fun onSuccess() {
                    closeLoginBottomSheet()
                    openRegisterBottomSheet()
                }

                override fun onError() {
                    closeLoginBottomSheet()
                }
            }

            setLoginListener(loginListener)
            show(supportFragmentManager, LoginBottomSheetFragment.TAG)
        } ?: run {
            loginBottomSheet = LoginBottomSheetFragment()
            openLoginBottomSheet()
        }
    }

    private fun openRegisterBottomSheet() {
        registerBottomSheet?.run {
            val registerListener = object : RegisterListener {
                override fun onAgree() {
                    closeRegisterBottomSheet()
                    //닉네임 등록화면 또는 홈화면으로 넝머가는 로직
                    nextActivity()
                }

                override fun onCancel() {
                    closeRegisterBottomSheet()
                }
            }

            setRegisterListener(registerListener)
            show(supportFragmentManager, RegisterBottomSheetFragment.TAG)
        } ?: run {
            registerBottomSheet = RegisterBottomSheetFragment()
            openRegisterBottomSheet()
        }
    }

    private fun closeLoginBottomSheet() {
        loginBottomSheet?.dismiss()
        loginBottomSheet = null
    }

    private fun closeRegisterBottomSheet() {
        registerBottomSheet?.dismiss()
        registerBottomSheet = null
    }

    private fun nextActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}