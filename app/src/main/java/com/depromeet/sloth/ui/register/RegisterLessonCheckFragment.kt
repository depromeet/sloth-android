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
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

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
        binding.tbLayout.tbRegisterLesson.setNavigationOnClickListener{
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() = with(registerLessonViewModel) {
        repeatOnStarted {
            launch {
                registerLessonEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success -> {
                                showToast(requireContext(), getString(R.string.lesson_register_complete))
                                val action =
                                    RegisterLessonCheckFragmentDirections.actionRegisterLessonCheckToLessonList()
                                findNavController().safeNavigate(action)
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext(), this@RegisterLessonCheckFragment) {
                                        registerLessonViewModel.removeAuthToken()
                                    }

                                    else -> {
                                        Timber.tag("Register Error").d(result.throwable)
                                        showToast(requireContext(), getString(R.string.lesson_register_fail))
                                    }
                                }
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
