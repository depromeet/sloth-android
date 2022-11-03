package com.depromeet.sloth.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityLoginBinding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val viewModel: LoginViewModel by viewModels()

    private var loginBottomSheet: LoginBottomSheetFragment? = null
    private var registerBottomSheet: RegisterBottomSheetFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = viewModel
        }

        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            lifecycleScope.launch {
                loginState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { loginState ->
                        when (loginState) {
                            true -> moveHomeActivity()
                            else -> Unit
                        }
                    }
            }

            lifecycleScope.launch {
                openLoginBottomSheetEvent
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { openLoginBottomSheet() }
            }
        }
    }

    private fun openLoginBottomSheet() {
        loginBottomSheet?.run {
            val loginListener = object : LoginListener {
                override fun onSuccessWithRegisteredMember() {
                    closeLoginBottomSheet()
                    moveHomeActivity()
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
                    moveHomeActivity()
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

    private fun moveHomeActivity() {
        startActivity(
            Intent(this, HomeActivity::class.java)
        )
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        closeLoginBottomSheet()
        closeRegisterBottomSheet()
    }
}