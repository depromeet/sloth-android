package com.depromeet.sloth.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLessonDetailBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showToast
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LessonDetailFragment :
    BaseFragment<FragmentLessonDetailBinding>(R.layout.fragment_lesson_detail) {

    private val lessonDetailViewModel: LessonDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = lessonDetailViewModel
        }
        initListener()
        initObserver()
    }

    private fun initListener() = with(binding) {
        tbLessonDetail.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() = with(lessonDetailViewModel) {
        repeatOnStarted {

            launch {
                deleteLessonSuccess
                    .collect {
                        navigateToLessonList()
                    }
            }

            launch {
                navigateToUpdateLessonEvent
                    .collect { lessonDetail ->
                        val action =
                            LessonDetailFragmentDirections.actionLessonDetailToUpdateLesson(
                                lessonDetail
                            )
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                navigateToDeleteLessonDialogEvent
                    .collect {
                        showLessonDeleteDialog()
                    }
            }

            launch {
                isLoading
                    .collect { isLoading ->
                        when (isLoading) {
                            true -> showProgress()
                            false -> hideProgress()
                        }
                    }
            }

            launch {
                showForbiddenDialogEvent
                    .collect {
                        showForbiddenDialog(
                            requireContext(),
                            this@LessonDetailFragment
                        ) { removeAuthToken() }
                    }
            }

            launch {
                showToastEvent
                    .collect { message ->
                        showToast(requireContext(), message)
                    }
            }
        }
    }

    private fun navigateToLessonList() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

    private fun showLessonDeleteDialog() {
        val dlg = SlothDialog(requireContext(), DialogState.DELETE_LESSON)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                lessonDetailViewModel.deleteLesson()
            }
        }
        dlg.show()
    }
}