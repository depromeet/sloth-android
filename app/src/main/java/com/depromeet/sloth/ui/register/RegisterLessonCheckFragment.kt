package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.repeatOnStarted
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
        initNavigation()
    }

    private fun initObserver() {
        viewModel.apply {
            repeatOnStarted {
                lessonRegisterState.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> showProgress()

                        is UiState.Success -> {
                            Timber.tag("Register Success").d("${uiState.data}")
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
                        else -> Unit
                    }
                    hideProgress()
                }
            }
        }
    }

    private fun initNavigation() {
        // navigation 은 repeatOnStarted 확장 함수를 통해 구독할 경우 에러 발생
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigateToRegisterLessonSecond.collect {
                    navigateToRegisterLessonSecond()
                }
            }
        }
    }

    private fun navigateToRegisterLessonSecond() {
        findNavController().navigateUp()
    }
}