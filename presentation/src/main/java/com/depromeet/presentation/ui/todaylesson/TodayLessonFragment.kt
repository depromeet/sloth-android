package com.depromeet.presentation.ui.todaylesson

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.adapter.TodayLessonAdapter
import com.depromeet.presentation.databinding.FragmentTodayLessonBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseFragment
import com.depromeet.presentation.ui.custom.LessonItemDecoration
import com.depromeet.presentation.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val viewModel: TodayLessonViewModel by viewModels()

    private val todayLessonItemClickListener = TodayLessonItemClickListener(
        onClick = { viewModel.navigateToRegisterLesson(R.id.today_lesson) },
        onPlusClick = { lesson -> viewModel.updateLessonCount(1, lesson) },
        onMinusClick = { lesson -> viewModel.updateLessonCount(-1, lesson) },
        onFinishClick = { lesson -> viewModel.navigateToFinishLessonDialog(lesson.lessonId.toString()) }
    )

    private val todayLessonAdapter by lazy {
        TodayLessonAdapter(todayLessonItemClickListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            vm = viewModel
        }
        initViews()
        initListener()
        initObserver()
    }

    override fun initViews() {
        binding.rvTodayLesson.apply {
            if (itemDecorationCount == 0) {
                addItemDecoration(LessonItemDecoration(requireContext(), 16))
            }
            adapter = todayLessonAdapter
        }
    }

    private fun initListener() {
        binding.tbTodayLesson.setOnMenuItemSingleClickListener {
            when (it.itemId) {
                R.id.menu_notification_list -> {
                    viewModel.onNotificationClicked()
                    true
                }
                else -> false
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.autoLoginEvent.collect { isLoggedIn ->
                    if (isLoggedIn) {
                        viewModel.checkTodayLessonOnBoardingComplete()
                    } else {
                        navigateToLogin()
                    }
                }
            }

            launch {
                viewModel.checkTodayLessonOnBoardingCompleteEvent.collect { isOnBoardingCompleted ->
                    if (isOnBoardingCompleted) {
                        viewModel.fetchTodayLessonList()
                    } else {
                        navigateToOnBoardingTodayLesson()
                    }
                }
            }

            launch {
                viewModel.navigateToLoginEvent.collect {
                    val action = TodayLessonFragmentDirections.actionTodayLessonToLogin()
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                viewModel.todayLessonUiModelList.collect {
                    todayLessonAdapter.submitList(it)
                }
            }

            launch {
                viewModel.navigateToWaitDialogEvent.collect {
                    navigateToNotificationList()
                }
            }

            launch {
                viewModel.navigateToFinishLessonDialogEvent.collect { lessonId ->
                    showFinishLessonDialog(lessonId)
                }
            }

            launch {
                viewModel.navigateRegisterLessonEvent.collect { fragmentId ->
                    val action = TodayLessonFragmentDirections.actionTodayLessonToRegisterLessonFirst(fragmentId)
                    findNavController().safeNavigate(action)
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

            launch {
                viewModel.navigateToNotificationListEvent.collect {
                    navigateToNotificationList()
                }
            }
        }
    }

    private fun navigateToLogin() {
        val action = TodayLessonFragmentDirections.actionTodayLessonToLogin()
        findNavController().safeNavigate(action)
    }

    private fun navigateToOnBoardingTodayLesson() {
        val action = TodayLessonFragmentDirections.actionTodayLessonToOnBoardingTodayLesson()
        findNavController().safeNavigate(action)
    }

    private fun navigateToNotificationList() {
        val action = TodayLessonFragmentDirections.actionTodayLessonToNotificationList()
        findNavController().safeNavigate(action)
    }

    private fun showFinishLessonDialog(lessonId: String) {
        val action = TodayLessonFragmentDirections.actionTodayLessonToFinishLessonDialog(lessonId)
        findNavController().safeNavigate(action)
    }
}