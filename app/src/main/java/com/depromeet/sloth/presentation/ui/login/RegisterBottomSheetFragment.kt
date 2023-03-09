package com.depromeet.sloth.presentation.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterBottomDialogBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.ui.base.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterBottomSheetFragment :
    BaseBottomSheetFragment<FragmentRegisterBottomDialogBinding>(R.layout.fragment_register_bottom_dialog) {

    private val viewModel: LoginViewModel by hiltNavGraphViewModels(R.id.nav_main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initObserver()
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.checkTodayLessonOnBoardingCompleteEvent.collect { isOnBoardingComplete ->
                    if (!isOnBoardingComplete) {
                        navigateToTodayLessonOnBoarding()
                    } else {
                        navigateToTodayLesson()
                    }
                }
            }

            launch {
                viewModel.navigateToPrivatePolicyEvent.collect {
                    showPrivatePolicy()
                }
            }

            launch {
                viewModel.registerAgreeEvent.collect {
                    viewModel.createAndRegisterNotificationToken()
                }
            }

            launch {
                viewModel.registerCancelEvent.collect {
                    closeRegisterBottomSheet()
                }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun navigateToTodayLessonOnBoarding() {
        val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomDialogToOnBoardingTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun navigateToTodayLesson() {
        val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomDialogToTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun closeRegisterBottomSheet() {
        val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomDialogToLogin()
        findNavController().safeNavigate(action)
    }

    private fun showPrivatePolicy() {
        val action = RegisterBottomSheetFragmentDirections.actionRegisterBottomDialogToSlothPolicyWebview()
        findNavController().safeNavigate(action)
    }
}