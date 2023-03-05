package com.depromeet.sloth.presentation.screen.todaylesson

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentFinishLessonDialogBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.screen.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FinishLessonDialogFragment :
    BaseDialogFragment<FragmentFinishLessonDialogBinding>(R.layout.fragment_finish_lesson_dialog) {

    private val viewModel: FinishLessonViewModel by viewModels()

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
                viewModel.finishLessonSuccessEvent.collect {
                        navigateToTodayLesson()
                    }
            }

            launch {
                viewModel.finishLessonCancelEvent.collect {
                        closeFinishLessonDialog()
                    }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.navigateToExpireDialogEvent.collect {
                        showExpireDialog()
                    }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun navigateToTodayLesson() {
        val action = FinishLessonDialogFragmentDirections.actionFinishLessonDialogToTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun closeFinishLessonDialog() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }
}