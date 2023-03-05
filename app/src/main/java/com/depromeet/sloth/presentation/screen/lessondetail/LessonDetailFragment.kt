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

    private val viewModel: LessonDetailViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.fetchLessonDetail()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initListener()
        initObserver()
    }

    private fun initListener() {
        binding.tbLessonDetail.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.navigateToUpdateLessonEvent.collect { lessonDetail ->
                        val action = LessonDetailFragmentDirections.actionLessonDetailToUpdateLesson(
                                lessonDetail
                            )
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                viewModel.navigateToDeleteLessonDialogEvent.collect {
                        showLessonDeleteDialog(viewModel.lessonId)
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

    private fun showLessonDeleteDialog(lessonId: String) {
        val action = LessonDetailFragmentDirections.actionLessonDetailToDeleteLessonDialog(
            lessonId
        )
        findNavController().safeNavigate(action)
    }
}