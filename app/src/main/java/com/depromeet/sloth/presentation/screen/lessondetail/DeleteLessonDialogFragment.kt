package com.depromeet.sloth.presentation.screen.lessondetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentDeleteLessonDialogBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.screen.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DeleteLessonDialogFragment :
    BaseDialogFragment<FragmentDeleteLessonDialogBinding>(R.layout.fragment_delete_lesson_dialog) {

    private val viewModel: DeleteLessonViewModel by viewModels()

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
                viewModel.deleteLessonSuccessEvent.collect {
                        navigateToLessonList()
                    }
            }

            launch {
                viewModel.deleteLessonCancelEvent.collect {
                        closeDeleteLessonDialog()
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

    private fun closeDeleteLessonDialog() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

    private fun navigateToLessonList() {
        val action = DeleteLessonDialogFragmentDirections.actionDeleteLessonDialogToLessonList()
        findNavController().safeNavigate(action)
    }
}