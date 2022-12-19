package com.depromeet.sloth.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityLoginBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.home.HomeActivity
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    private var loginBottomSheet: LoginBottomSheetFragment? = null
    private var registerBottomSheet: RegisterBottomSheetFragment? = null

    private val deviceId: String by lazy {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = loginViewModel
        }
        initObserver()
    }

    private fun initObserver() = with(loginViewModel) {
        repeatOnStarted {
            launch {
                autoLoginEvent
                    .collect { loginState ->
                        when (loginState) {
                            true -> loginViewModel.createAndRegisterNotificationToken(deviceId)
                            else -> Unit
                        }
                    }

            }

            launch {
                navigateToLoginBottomSheetEvent
                    .collect {
                        showLoginBottomSheet()
                    }
            }

            // 로그인이 성공하면 토큰을 서버에 전달해주는 방식으로 로직 변경
            // 토큰을 전달한 다음 홈 화면으로 이동
            launch {
                registerNotificationTokenEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> {
                                Timber.d(result.data)
                                startActivity(
                                    Intent(this@LoginActivity, HomeActivity::class.java)
                                )
                                finish()
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(this@LoginActivity) {
                                        removeAuthToken()
                                    }
                                    else -> Timber.tag("Register Error").d(result.throwable)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun showLoginBottomSheet() {
        loginBottomSheet?.run {
            val loginListener = object : LoginListener {
                override fun onSuccessWithRegisteredMember() {
                    closeLoginBottomSheet()
                    loginViewModel.createAndRegisterNotificationToken(deviceId)
                }

                override fun onSuccessWithNewMember() {
                    closeLoginBottomSheet()
                    showRegisterBottomSheet()
                }

                override fun onError() {
                    closeLoginBottomSheet()
                }
            }

            setLoginListener(loginListener)
            show(supportFragmentManager, LoginBottomSheetFragment.TAG)
        } ?: run {
            loginBottomSheet = LoginBottomSheetFragment()
            showLoginBottomSheet()
        }
    }

    private fun showRegisterBottomSheet() {
        registerBottomSheet?.run {
            val registerListener = object : RegisterListener {
                override fun onAgree() {
                    closeRegisterBottomSheet()
                    loginViewModel.createAndRegisterNotificationToken(deviceId)
                }

                override fun onCancel() {
                    closeRegisterBottomSheet()
                }
            }

            setRegisterListener(registerListener)
            show(supportFragmentManager, RegisterBottomSheetFragment.TAG)
        } ?: run {
            registerBottomSheet = RegisterBottomSheetFragment()
            showRegisterBottomSheet()
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

    override fun onDestroy() {
        closeLoginBottomSheet()
        closeRegisterBottomSheet()
        super.onDestroy()
    }
}