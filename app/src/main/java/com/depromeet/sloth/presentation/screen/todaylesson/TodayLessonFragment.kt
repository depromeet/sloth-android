package com.depromeet.sloth.presentation.screen.todaylesson

import TodayLessonAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentTodayLessonBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


// TODO list 를 viewModel 내에서 관리하여, 변경이 발생할 경우 매번 재 호출하는 것이 아닌, list 를 update 하는 방식으로 변경
// TODO updateLessonCount API, finishLesson Api 를 호출하는 로직과 리스트 갱신을 분리
// TODO API 호출을 통한 결과로 리스트를 처음부터 다시 세팅해주지 말고, 기존의 리스트를 갱신하는 식으로 변경
// TODO uiState + stateFlow 를 통한 이벤트 처리
@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val todayLessonViewModel: TodayLessonViewModel by viewModels()

    private val todayLessonItemClickListener = TodayLessonItemClickListener(
        onClick = { todayLessonViewModel.navigateToRegisterLesson(R.id.today_lesson) },
        onPlusClick = { lesson -> todayLessonViewModel.updateLessonCount(1, lesson) },
        onMinusClick = { lesson -> todayLessonViewModel.updateLessonCount(-1, lesson) },
        onFinishClick = { lesson -> todayLessonViewModel.navigateToFinishLessonDialog(lesson.lessonId.toString()) }
    )

    private val todayLessonAdapter by lazy {
        TodayLessonAdapter(todayLessonItemClickListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            vm = todayLessonViewModel
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

    private fun initListener() = with(binding) {
        tbTodayLesson.apply {
            setOnMenuItemSingleClickListener {
                when (it.itemId) {
                    R.id.menu_notification_list -> {
                        todayLessonViewModel.navigateToWaitDialog()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initObserver() = with(todayLessonViewModel) {
        repeatOnStarted {
            launch {
                autoLoginEvent.collect { isLoggedIn ->
                    if (!isLoggedIn) {
                        val action = TodayLessonFragmentDirections.actionTodayLessonToLogin()
                        findNavController().safeNavigate(action)
                    } else {
                        todayLessonViewModel.checkTodayLessonOnBoardingComplete()
                    }
                }
            }
            launch {
                checkTodayLessonOnBoardingCompleteEvent.collect { isOnBoardingComplete ->
                    if (!isOnBoardingComplete) {
                        val action = TodayLessonFragmentDirections.actionTodayLessonToOnBoardingTodayLesson()
                        findNavController().safeNavigate(action)
                    } else {
                        todayLessonViewModel.fetchTodayLessonList()
                    }
                }
            }

            launch {
                todayLessonUiModelList.collect {
                    todayLessonAdapter.submitList(it)
                }
            }

            launch {
                navigateToWaitDialogEvent.collect {
                    showWaitDialog()
                }
            }

            launch {
                navigateToFinishLessonDialogEvent.collect { lessonId ->
                    showFinishLessonDialog(lessonId)
                }
            }

            launch {
                navigateRegisterLessonEvent.collect { fragmentId ->
                    val action = TodayLessonFragmentDirections.actionTodayLessonToRegisterLessonFirst(fragmentId)
                    findNavController().safeNavigate(action)
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

    private fun showWaitDialog() {
        val action = TodayLessonFragmentDirections.actionTodayLessonToWaitDialog()
        findNavController().safeNavigate(action)
    }

    private fun showFinishLessonDialog(lessonId: String) {
        val action = TodayLessonFragmentDirections.actionTodayLessonToFinishLessonDialog(lessonId)
        findNavController().safeNavigate(action)
    }
}