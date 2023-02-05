package com.depromeet.sloth.presentation.screen.lessondetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLessonDetailBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LessonDetailFragment :
    BaseFragment<FragmentLessonDetailBinding>(R.layout.fragment_lesson_detail) {

    private val lessonDetailViewModel: LessonDetailViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        lessonDetailViewModel.fetchLessonDetail()
    }

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
                        showLessonDeleteDialog(lessonDetailViewModel.lessonId)
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
                navigateToExpireDialogEvent
                    .collect {
                        showExpireDialog()
                    }
            }

            launch {
                showToastEvent
                    .collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun showLessonDeleteDialog(lessonId: String) {
        val action = LessonDetailFragmentDirections.actionLessonDetailToDeleteLessonDialog(
            lessonId
        )
        findNavController().safeNavigate(action)
    }
}