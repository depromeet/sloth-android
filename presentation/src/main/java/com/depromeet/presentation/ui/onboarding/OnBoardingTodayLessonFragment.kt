package com.depromeet.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.adapter.OnBoardingAdapter
import com.depromeet.presentation.databinding.FragmentOnBoardingTodayLessonBinding
import com.depromeet.presentation.ui.base.BaseFragment
import com.depromeet.presentation.ui.custom.LessonItemDecoration
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OnBoardingTodayLessonFragment : BaseFragment<FragmentOnBoardingTodayLessonBinding>(R.layout.fragment_on_boarding_today_lesson) {

    private val viewModel: OnBoardingTodayLessonViewModel by viewModels()

    private val onBoardingItemClickListener = OnBoardingItemClickListener(
        onClick = { viewModel.navigateToRegisterLesson(R.id.on_boarding_today_lesson) },
        onPlusClick = { viewModel.updateOnBoardingItemCount(1) },
        onMinusClick = { viewModel.updateOnBoardingItemCount(-1) },
        onFinishClick = { viewModel.navigateToOnBoardingLessonRegisterDialog() }
    )

    private val onBoardingAdapter by lazy {
        OnBoardingAdapter(onBoardingItemClickListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }

        // 시스템 백 버튼 비활성화
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { return@addCallback }

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

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.navigateToOnBoardingClickPlusDialogEvent.collect {
                    showOnBoardingClickPlusDialog()
                }
            }

            launch {
                viewModel.navigateToOnBoardingRegisterLessonDialogEvent.collect {
                    showOnBoardingRegisterLessonDialog()
                }
            }

            launch {
                viewModel.navigateToRegisterLessonEvent.collect { fragmentId ->
                    val action = OnBoardingTodayLessonFragmentDirections.actionOnBoardingTodayLessonToNavRegisterLesson(fragmentId)
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                viewModel.onBoardingUiModelList.collect {
                    onBoardingAdapter.submitList(it)
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

    private fun showOnBoardingClickPlusDialog() {
        val action = OnBoardingTodayLessonFragmentDirections.actionOnBoardingTodayLessonToOnBoardingClickPlusDialog()
        findNavController().safeNavigate(action)
    }

    private fun showOnBoardingRegisterLessonDialog() {
        val action = OnBoardingTodayLessonFragmentDirections.actionOnBoardingTodayLessonToOnBoardingRegisterLessonDialog()
        findNavController().safeNavigate(action)
    }
}