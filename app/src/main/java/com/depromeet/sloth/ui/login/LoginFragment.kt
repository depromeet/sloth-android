package com.depromeet.sloth.ui.login

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLoginBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO 로그인 단계인데 로그아웃 처리를 해주는 부분 확인
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_home)

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
                registerNotificationTokenSuccess
                    .collect {
                        navigateToTodayLesson()
                    }
            }

            launch {
                registerNotificationTokenFail
                    .collect { statusCode ->
                        when (statusCode) {
                            401 -> showForbiddenDialog(
                                requireContext(),
                                this@LoginFragment
                            ) {
                                removeAuthToken()
                            }

                            else -> Timber.d("Register Error")
                        }
                    }
            }

            launch {
                navigateToLoginBottomSheetEvent
                    .collect { showLoginBottomSheet() }
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