package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterLessonCheckFragment :
    BaseFragment<FragmentRegisterLessonCheckBinding>(R.layout.fragment_register_lesson_check) {

    private val viewModel: RegisterLessonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
            lesson = viewModel.lessonCheck.value
        }
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                registerLessonState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success -> {
                                showToast(getString(R.string.lesson_register_complete))
                                requireActivity().finish()
                            }
                            is UiState.Unauthorized -> {
                                showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
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

            viewLifecycleOwner.lifecycleScope.launch {
                onNavigateToRegisterLessonSecondClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { navigateToRegisterLessonSecond() }
            }
        }
    }

    private fun navigateToRegisterLessonSecond() {
        findNavController().navigateUp()
    }
}
