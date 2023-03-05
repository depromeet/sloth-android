package com.depromeet.sloth.presentation.screen.registerlesson

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterLessonCheckFragment : BaseFragment<FragmentRegisterLessonCheckBinding>(R.layout.fragment_register_lesson_check) {

    private val viewModel: RegisterLessonViewModel by hiltNavGraphViewModels(R.id.nav_register_lesson)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initListener()
        initObserver()
    }

    private fun initListener() {
        binding.tbLayout.tbRegisterLesson.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.registerLessonSuccessEvent.collect { fragmentId ->
                    when (fragmentId) {
                        R.id.today_lesson -> {
                            val action = RegisterLessonCheckFragmentDirections.actionRegisterLessonCheckToLessonList()
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.today_lesson, true)
                                .build()
                            findNavController().navigate(action, navOptions)
                        }

                        R.id.lesson_list -> {
                            val action = RegisterLessonCheckFragmentDirections.actionRegisterLessonCheckToLessonList()
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.lesson_list, true)
                                .build()
                            findNavController().navigate(action, navOptions)
                        }

                        R.id.on_boarding_today_lesson -> {
                            val action = RegisterLessonCheckFragmentDirections.actionRegisterLessonCheckToLessonList()
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.on_boarding_today_lesson, true)
                                .build()
                            findNavController().navigate(action, navOptions)
                        }
                    }
                }
            }

            launch {
                viewModel.navigateToRegisterLessonSecondEvent.collect {
                    findNavController().navigateUp()
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
}
