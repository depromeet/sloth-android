package com.depromeet.sloth.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentRegisterLessonCheckBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterLessonCheckFragment :
    BaseFragment<FragmentRegisterLessonCheckBinding>(R.layout.fragment_register_lesson_check) {

    private val registerLessonViewModel: RegisterLessonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = registerLessonViewModel
        }
        initObserver()
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
                                showToast(getString(R.string.lesson_register_complete))
                                requireActivity().finish()
                            }
                            is Result.Error -> {
                                when(result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                            registerLessonViewModel.removeAuthToken()
                                        }

                                    else -> {
                                        Timber.tag("Register Error").d(result.throwable)
                                        showToast(getString(R.string.lesson_register_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                navigateToRegisterLessonSecondEvent
                    .collect {
                        navigateToRegisterLessonSecond()
                    }
            }
        }
    }

    private fun navigateToRegisterLessonSecond() {
        findNavController().navigateUp()
    }
}
