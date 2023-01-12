package com.depromeet.sloth.presentation.finish

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentFinishLessonDialogBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showExpireDialog
import com.depromeet.sloth.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FinishLessonDialogFragment :
    BaseDialogFragment<FragmentFinishLessonDialogBinding>(R.layout.fragment_finish_lesson_dialog) {

    private val finishLessonViewModel: FinishLessonViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            vm = finishLessonViewModel
        }
        initObserver()
    }

    private fun initObserver() = with(finishLessonViewModel) {

        repeatOnStarted {
            launch {
                finishLessonSuccessEvent
                    .collect {
                        navigateToTodayLesson()
                    }
            }

            launch {
                finishLessonCancelEvent
                    .collect {
                        closeFinishLessonDialog()
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
                        showExpireDialog(this@FinishLessonDialogFragment)
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