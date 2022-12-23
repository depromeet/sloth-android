package com.depromeet.sloth.ui.login

import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLoginBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO 로그인 단계인데 로그아웃 처리를 해주는 부분 확인
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_home)

    private val deviceId: String by lazy {
        Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                            true -> createAndRegisterNotificationToken(deviceId)
                            else -> Unit
                        }
                    }
            }

            launch {
                navigateToLoginBottomSheetEvent
                    .collect { showLoginBottomSheet() }
            }

            // 로그인이 성공하면 토큰을 서버에 전달해주는 방식으로 로직 변경
            // 토큰을 전달한 다음 홈 화면으로 이동
            launch {
                registerNotificationTokenEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> { navigateToTodayLesson() }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext(), this@LoginFragment) {
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
        val action = LoginFragmentDirections.actionLoginToLoginBottom()
        findNavController().safeNavigate(action)
    }

    private fun navigateToTodayLesson() {
        val action = LoginFragmentDirections.actionLoginToTodayLesson()
        findNavController().safeNavigate(action)
    }
}