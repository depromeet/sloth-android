package com.depromeet.sloth.ui.login

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterBottomBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class RegisterBottomSheetFragment :
    BaseBottomSheetFragment<FragmentRegisterBottomBinding>(R.layout.fragment_register_bottom) {

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
                                this@RegisterBottomSheetFragment
                            ) {
                                removeAuthToken()
                            }

                            else -> Timber.d("Register Error")
                        }
                    }
            }

            launch {
                navigateToPrivatePolicyEvent.collect {
                    showPrivatePolicy()
                }
            }

            launch {
                registerAgreeEvent.collect {
                    createAndRegisterNotificationToken()
                }
            }

            launch {
                registerCancelEvent.collect {
                    closeRegisterBottomSheet()
                }
            }

        }
    }


    private fun navigateToTodayLesson() {
        val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomToTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun closeRegisterBottomSheet() {
        val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomToLogin()
        findNavController().safeNavigate(action)
    }

    private fun showPrivatePolicy() {
        val action =
            RegisterBottomSheetFragmentDirections.actionRegisterBottomToSlothPolicyWebview()
        findNavController().safeNavigate(action)
    }
}