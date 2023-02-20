package com.depromeet.sloth.presentation.screen.todaylesson

import OnBoardingAdapter
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
// TODO 온보딩이 후에 강의 목록화면에서 투데이 화면으로 메뉴 버튼을 눌러도 이동이 안되는 이슈
@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val todayLessonViewModel: TodayLessonViewModel by viewModels()

    private val todayLessonItemClickListener = TodayLessonItemClickListener(
        onClick = { todayLessonViewModel.navigateToRegisterLesson() },
        onPlusClick = { lesson -> todayLessonViewModel.updateLessonCount(1, lesson) },
        onMinusClick = { lesson -> todayLessonViewModel.updateLessonCount(-1, lesson) },
        onFinishClick = { lesson -> todayLessonViewModel.navigateToFinishLessonDialog(lesson.lessonId.toString()) }
    )

    private val onBoardingItemClickListener = OnBoardingItemClickListener(
        // onPlusClick = { lesson -> todayLessonViewModel.updateOnBoardingItemCount(1, lesson.lessonId.toString()) },
        onPlusClick = {},
        // onMinusClick = { lesson -> todayLessonViewModel.updateOnBoardingItemCount(-1, lesson.lessonId.toString()) },
        onMinusClick = {},
        onFinishClick = { todayLessonViewModel.navigateToOnBoardingLessonRegisterDialog() }
    )

    private val todayLessonAdapter by lazy {
        TodayLessonAdapter(todayLessonItemClickListener)
    }

    private val onBoardingAdapter by lazy {
        OnBoardingAdapter(onBoardingItemClickListener)
    }

    override fun onStart() {
        super.onStart()
        todayLessonViewModel.fetchTodayLessonList()
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
                    }
                }
            }
            launch {
                checkTodayLessonOnBoardingCompleteEvent.collect { isOnBoardingComplete ->
                    if (!isOnBoardingComplete) showOnBoardingClickPlusDialog()
                }
            }

            launch {
                navigateToOnBoardingRegisterLessonDialogEvent.collect {
                    showOnBoardingRegisterLessonDialog()
                }
            }

            launch {
                todayLessonList.collect {
                    todayLessonAdapter.submitList(it)
                }
            }

            launch {
                onBoardingList.collect {
                    onBoardingAdapter.submitList(it)
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
                navigateRegisterLessonEvent.collect {
                        val action = TodayLessonFragmentDirections.actionTodayLessonToRegisterLessonFirst()
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
    private fun showOnBoardingClickPlusDialog() {
        val action = TodayLessonFragmentDirections.actionTodayLessonToOnBoardingClickPlusDialog()
        findNavController().safeNavigate(action)
    }

    private fun showOnBoardingRegisterLessonDialog() {
        val action = TodayLessonFragmentDirections.actionTodayLessonToOnBoardingRegisterLessonDialog()
        findNavController().safeNavigate(action)
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