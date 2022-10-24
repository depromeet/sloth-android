package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint
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
            lessonRegisterState.observe(viewLifecycleOwner,
                EventObserver { lessonRegisterResponse ->
                    when (lessonRegisterResponse) {
                        is LessonState.Loading -> handleLoadingState(requireContext())

                        is LessonState.Success -> {
                            Timber.tag("Register Success").d("${lessonRegisterResponse.data}")
                            showToast(getString(R.string.lesson_register_complete))
                            (activity as RegisterLessonActivity).finish()
                        }

                        is LessonState.Unauthorized -> {
                            showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                        }

                        is LessonState.Error -> {
                            Timber.tag("Register Error").d(lessonRegisterResponse.throwable)
                            showToast(getString(R.string.lesson_register_fail))
                        }
                        else -> Unit
                    }
                    hideProgress()
                })
        }
    }

    private fun initNavigation() {
        viewModel.moveRegisterLessonSecondEvent.observe(viewLifecycleOwner, EventObserver {
            moveRegisterLessonSecond()
        })
    }

    private fun moveRegisterLessonSecond() {
        findNavController().navigateUp()
    }
}