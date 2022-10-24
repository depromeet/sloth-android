package com.depromeet.sloth.ui.login

import android.content.Intent
import android.os.Bundle
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.databinding.ActivityLoginBinding
import com.depromeet.sloth.ui.HomeActivity
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    private var loginBottomSheet: LoginBottomSheetFragment? = null
    private var registerBottomSheet: RegisterBottomSheetFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(preferenceManager.getAccessToken().isNotEmpty() && preferenceManager.getRefreshToken().isNotEmpty()) {
            nextActivity()
        } else {
            binding.btnLoginStart.setOnClickListener {
                openLoginBottomSheet()
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        closeLoginBottomSheet()
        closeRegisterBottomSheet()
    }
}