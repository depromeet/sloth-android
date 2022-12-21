package com.depromeet.sloth.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLoginBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.home.HomeActivity
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    // private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_login)
    private val loginViewModel: LoginViewModel by activityViewModels()

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
            registerNotificationTokenEvent
                .collect { result ->
                    when (result) {
                        is Result.Loading -> showProgress()
                        is Result.UnLoading -> hideProgress()
                        is Result.Success<String> -> {
                            Timber.d(result.data)

                            startActivity(
                                Intent(requireContext(), HomeActivity::class.java)
                            )
                            requireActivity().finish()

                            // TODO navigateToLessonTodayFragment
                            // findNavController().navigate(R.id.action_nav_login_to_today_lesson)
                        }
                        is Result.Error -> {
                            when (result.statusCode) {
                                401 -> showForbiddenDialog(requireContext()) {
                                    removeAuthToken()
                                }
                                else -> Timber.tag("Register Error").d(result.throwable)
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
}