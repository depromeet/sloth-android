package com.depromeet.sloth.presentation.detail

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentDeleteLessonDialogBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DeleteLessonDialogFragment :
    BaseDialogFragment<FragmentDeleteLessonDialogBinding>(R.layout.fragment_delete_lesson_dialog) {

    private val lessonDetailViewModel: LessonDetailViewModel by hiltNavGraphViewModels(R.id.nav_main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = lessonDetailViewModel
        }
        initObserver()
    }

    private fun initObserver() = with(lessonDetailViewModel) {

        repeatOnStarted {
            launch {
                deleteLessonSuccessEvent
                    .collect {
                        navigateToLessonList()
                    }
            }

            launch {
                deleteLessonCancelEvent
                    .collect {
                        closeDeleteLessonDialog()
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