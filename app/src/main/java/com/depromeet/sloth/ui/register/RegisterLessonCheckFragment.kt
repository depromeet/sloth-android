package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterLessonCheckFragment :
    BaseFragment<FragmentRegisterLessonCheckBinding>(R.layout.fragment_register_lesson_check) {

    private val registerLessonViewModel: RegisterLessonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = registerLessonViewModel
        }
        initObserver()
    }

    private fun initObserver() = with(registerLessonViewModel) {
        repeatOnStarted {
            launch {
                registerLessonState
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success -> {
                                showToast(getString(R.string.lesson_register_complete))
                                requireActivity().finish()
                            }
                            is UiState.Unauthorized -> {
                                showForbiddenDialog(requireContext()) { registerLessonViewModel.removeAuthToken() }
                            }
                            is UiState.Error -> {
                                Timber.tag("Register Error").d(uiState.throwable)
                                showToast(getString(R.string.lesson_register_fail))
                            }
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            launch {
                onNavigateToRegisterLessonSecondClick
                    .collect {
                        navigateToRegisterLessonSecond()
                    }
            }
        }
    }

    private fun navigateToRegisterLessonSecond() {
        findNavController().navigateUp()
    }
}
