package com.depromeet.sloth.presentation.screen.onboarding

import OnBoardingAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentOnBoardingTodayLessonBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingTodayLessonFragment : BaseFragment<FragmentOnBoardingTodayLessonBinding>(R.layout.fragment_on_boarding_today_lesson) {

    private val onBoardingTodayLessonViewModel: OnBoardingTodayLessonViewModel by viewModels()

    private val onBoardingItemClickListener = OnBoardingItemClickListener(
        onClick = { onBoardingTodayLessonViewModel.navigateToRegisterLesson() },
        onPlusClick = { onBoardingTodayLessonViewModel.updateOnBoardingItemCount(1) },
        onMinusClick = { onBoardingTodayLessonViewModel.updateOnBoardingItemCount(-1) },
        onFinishClick = { onBoardingTodayLessonViewModel.navigateToOnBoardingLessonRegisterDialog() }
    )

    private val onBoardingAdapter by lazy {
        OnBoardingAdapter(onBoardingItemClickListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            vm = onBoardingTodayLessonViewModel
        }
        initViews()
        initObserver()
    }

    override fun initViews() {
        binding.rvTodayLesson.apply {
            if (itemDecorationCount == 0) {
                addItemDecoration(LessonItemDecoration(requireContext(), 16))
            }
            adapter = onBoardingAdapter
        }
    }

    private fun initObserver() = with(onBoardingTodayLessonViewModel) {
        repeatOnStarted {
            launch {
                navigateToOnBoardingClickPlusDialogEvent.collect {
                    showOnBoardingClickPlusDialog()
                }
            }

            launch {
                navigateToOnBoardingRegisterLessonDialogEvent.collect {
                    showOnBoardingRegisterLessonDialog()
                }
            }

            launch {
                navigateToRegisterLessonEvent.collect {
                    val action = OnBoardingTodayLessonFragmentDirections.actionOnBoardingTodayLessonToNavRegisterLesson()
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                onBoardingUiModelList.collect {
                    onBoardingAdapter.submitList(it)
                }
            }

            launch {
                isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                navigateToExpireDialogEvent.collect {
                    showExpireDialog()
                }
            }

            launch {
                showToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showOnBoardingClickPlusDialog() {
        val action = OnBoardingTodayLessonFragmentDirections.actionOnBoardingTodayLessonToOnBoardingClickPlusDialog()
        findNavController().safeNavigate(action)
    }

    private fun showOnBoardingRegisterLessonDialog() {
        val action = OnBoardingTodayLessonFragmentDirections.actionOnBoardingTodayLessonToOnBoardingRegisterLessonDialog()
        findNavController().safeNavigate(action)
    }
}