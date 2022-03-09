package com.depromeet.sloth.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.depromeet.sloth.databinding.ActivityLoginBinding
import android.widget.Button
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    private var loginBottomSheet: LoginBottomSheetFragment? = null
    private var registerBottomSheet: RegisterBottomSheetFragment? = null

    override fun getViewBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    companion object {
        fun newIntent(activity: Activity) =
            Intent(activity, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if(preferenceManager.getAccessToken().isNotEmpty() && preferenceManager.getRefreshToken().isNotEmpty()) {
            nextActivity()
        } else {
            findViewById<Button>(R.id.btn_login_start).setOnClickListener {
                openLoginBottomSheet()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeLoginBottomSheet()
        closeRegisterBottomSheet()
    }

    private fun openLoginBottomSheet() {
        loginBottomSheet?.run {
            val loginListener = object : LoginListener {
                override fun onSuccessWithRegisteredMember() {
                    closeLoginBottomSheet()
                    nextActivity()
                }

                override fun onSuccessWithNewMember() {
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
        finish()
    }
}