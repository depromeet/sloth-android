package com.depromeet.sloth.presentation.screen.todaylesson

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentTodayLessonBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.HeaderAdapter
import com.depromeet.sloth.presentation.adapter.TodayLessonAdapter
import com.depromeet.sloth.presentation.screen.LessonUiModel
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


// TODO list 를 viewModel 내에서 관리하여, 변경이 발생할 경우 매번 재 호출하는 것이 아닌, list 를 update 하는 방식으로 변경
// TODO updateLessonCount API, finishLesson Api 를 호출하는 로직과 리스트 갱신을 분리
// TODO API 호출을 통한 결과로 리스트를 처음부터 다시 세팅해주지 말고, 기존의 리스트를 갱신하는 식으로 변경
// TODO 온보딩이 후에 강의 목록화면에서 투데이 화면으로 메뉴 버튼을 눌러도 이동이 안되는 이슈
// TODO 오늘까지 들어야 하는 강의가 오늘까지 완료한 강의보다 밑으로 배치되는 이슈
@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val todayLessonViewModel: TodayLessonViewModel by viewModels()

    private val lessonItemClickListener = LessonItemClickListener(
        onClick = { todayLessonViewModel.navigateToRegisterLesson() },
        onPlusClick = { lesson -> todayLessonViewModel.updateLessonCount(1, lesson) },
        onMinusClick = { lesson -> todayLessonViewModel.updateLessonCount(-1, lesson) },
        onFinishClick = { lesson -> todayLessonViewModel.navigateToFinishLessonDialog(lesson.lessonId.toString()) }
    )

    private val todayLessonAdapter by lazy {
        TodayLessonAdapter(lessonItemClickListener)
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
//            launch {
//                checkOnBoardingCompleteEvent.collect { isOnBoardingComplete ->
//                    when (isOnBoardingComplete) {
//                        //TODO UDF 위반
//                        true -> fetchTodayLessonList()
//                        false -> showOnBoardingClickPlus()
//                    }
//                }
//            }
//
//            launch {
//                showOnBoardingClickPlusEvent.collect {
//                    setOnBoardingList()
//                    val action =
//                        TodayLessonFragmentDirections.actionTodayLessonToOnBoardingClickPlusDialog()
//                    findNavController().safeNavigate(action)
//                }
//            }
//            launch {
//                showOnBoardingRegisterLessonEvent.collect {
//                    // setLessonList(emptyList())
//                    val action =
//                        TodayLessonFragmentDirections.actionTodayLessonToOnBoardingRegisterLessonDialog()
//                    findNavController().safeNavigate(action)
//                }
//            }

            launch {
                todayLessonList.collect {
                    todayLessonAdapter.submitList(it)
                    initSloth(it)
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
                navigateRegisterLessonEvent
                    .collect {
                        val action =
                            TodayLessonFragmentDirections.actionTodayLessonToRegisterLessonFirst()
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                isLoading
                    .collect { isLoading ->
                        when (isLoading) {
                            true -> showProgress()
                            false -> hideProgress()
                        }
                    }
            }

            launch {
                navigateToExpireDialogEvent
                    .collect {
                        showExpireDialog()
                    }
            }

            launch {
                showToastEvent
                    .collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun initSloth(todayLessonList: List<LessonUiModel>) = with(binding) {
        // TODO list 가 분리되어 있지 않으므로 Title 과 이미지를 다른 기준을 정하여 뷰에 뿌려줘야 함
        // TODO 변수내의 list 복수형으로 변경
        // TODO isNotStart 상태도 설정 해야함
        val isEmpty = todayLessonList.contains(LessonUiModel.EmptyLesson)
        // TODO 시작하지 않았음을 어떻게 판별하지
        // 모든 item(todayLesson) 의 presentNumber 가 0 보다 크지 않아야
        // val isNotStart = !this.any { it.presentNumber > 0 }
        val isFinished = todayLessonList.contains(
            LessonUiModel.LessonHeader(
                HeaderAdapter.HeaderType.FINISHED,
                null
            )
        )
                && !todayLessonList.contains(
            LessonUiModel.LessonHeader(
                HeaderAdapter.HeaderType.DOING,
                null
            )
        )

        if (isEmpty) {
            tvTodayTitleMessage.text =
                getString(R.string.today_lesson_title_not_register)
            ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_not_start)
        } else if (isFinished) {
            tvTodayTitleMessage.text =
                getString(R.string.today_lesson_title_win)
            ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_lose)
//                } else if (isNotStart){
//                    tvTodayTitleMessage.text =
//                        getString(R.string.today_lesson_title_not_start)
//                    ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_not_start)
        } else {
            tvTodayTitleMessage.text =
                getString(R.string.today_lesson_title_lose)
            ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_win)
        }
    }

    private fun showWaitDialog() {
        val action = TodayLessonFragmentDirections.actionTodayLessonToWaitDialog()
        findNavController().safeNavigate(action)
    }

    private fun showFinishLessonDialog(lessonId: String) {
        val action =
            TodayLessonFragmentDirections.actionTodayLessonToFinishLessonDialog(lessonId)
        findNavController().safeNavigate(action)
    }
}