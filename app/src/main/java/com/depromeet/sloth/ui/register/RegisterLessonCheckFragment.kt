package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showToast
import com.depromeet.sloth.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterLessonCheckFragment :
    BaseFragment<FragmentRegisterLessonCheckBinding>(R.layout.fragment_register_lesson_check) {

    private val registerLessonViewModel: RegisterLessonViewModel by hiltNavGraphViewModels(R.id.nav_register_lesson)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = registerLessonViewModel
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

    private fun initObserver() = with(registerLessonViewModel) {
        repeatOnStarted {

            launch {
                registerLessonSuccess
                    .collect {
                        showToast(requireContext(), getString(R.string.lesson_register_complete))
                        val action =
                            RegisterLessonCheckFragmentDirections.actionRegisterLessonCheckToLessonList()
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                registerLessonFail
                    .collect { statusCode ->
                        when (statusCode) {
                            401 -> showForbiddenDialog(
                                requireContext(),
                                this@RegisterLessonCheckFragment
                            ) {
                                removeAuthToken()
                            }

                            else -> {
                                showToast(requireContext(), getString(R.string.lesson_register_fail))
                            }
                        }
                    }
            }

            launch {
                navigateToRegisterLessonSecondEvent
                    .collect {
                        findNavController().navigateUp()
                    }
            }
        }
    }
}
