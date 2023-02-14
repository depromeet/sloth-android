package com.depromeet.sloth.presentation.screen.todaylesson

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentTodayLessonBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.HeaderAdapter
import com.depromeet.sloth.presentation.adapter.OnBoardingAdapter
import com.depromeet.sloth.presentation.adapter.TodayLessonAdapter
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


// TODO list 를 viewModel 내에서 관리하여, 변경이 발생할 경우 매번 재호출하는 것이 아닌, list 를 update 하는 방식으로 변경
// TODO updateLessonCount API, finishLesson Api 를 호출하는 로직과 리스트 갱신을 분리
// TODO Header 도 viewType 으로 선언 (headerAdapter 의 headerType 제거)
// TODO concatAdapter.submitList ...
// TODO Empty일때 핸들링
// 근데 지금 짠대로 만약에 run 하면 뷰들이 뒤죽박죽 섞여있을 것 같다. 정렬이 수반되어야할 것 같은데.. 그게 가능한건가 (채팅과의 차이점)
// 각 뷰타입들이 모여있어야 하는데
// API 호출을 통한 결과로 리스트를 처음부터 다시 세팅해주지 말고, 기존의 리스트를 갱신하는 식으로 변경
// 온보딩이 후에 강의 목록화면에서 투데이 화면으로 메뉴 버튼을 눌러도 이동이 안되는 이슈
@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val todayLessonViewModel: TodayLessonViewModel by viewModels()

    private lateinit var concatAdapter: ConcatAdapter

    private val emptyHeader by lazy { HeaderAdapter(HeaderAdapter.HeaderType.EMPTY) }

    private val emptyLessonAdapter by lazy {
        TodayLessonAdapter {
            todayLessonViewModel.navigateToRegisterLesson()
        }
    }

    private val doingHeader by lazy { HeaderAdapter(HeaderAdapter.HeaderType.DOING) }

    // TODO 뷰모델 내의 함수를 어댑터의 생성자로 전달
    private val doingLessonAdapter by lazy { TodayLessonAdapter {} }

    private val finishedHeader by lazy { HeaderAdapter(HeaderAdapter.HeaderType.FINISHED) }

    private val finishedLessonAdapter by lazy { TodayLessonAdapter {} }

    private val doingOnBoardingAdapter by lazy { OnBoardingAdapter{} }

    private val finishedOnBoardingAdapter by lazy { OnBoardingAdapter{}}

    override fun onStart() {
        super.onStart()
        todayLessonViewModel.fetchTodayLessonList()
        // TODO fetchTodayLesson 함수 내로 이동
        // todayLessonViewModel.checkOnBoardingComplete()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            vm = todayLessonViewModel
        }
        initListener()
        initObserver()
    }

    override fun initViews() {
        todayLessonViewModel.apply {
            val isOnBoardingComplete = onBoardingComplete.value
            if (isOnBoardingComplete) {
                val isEmpty = lessonEmptyList.value.isNotEmpty()
                val isNotStart =
                    lessonDoingList.value.isNotEmpty() && lessonFinishedList.value.isEmpty()
                lessonDoingList.value.any { it.presentNumber > 0 }.not()
                val isFinished =
                    lessonDoingList.value.isEmpty() && lessonFinishedList.value.isNotEmpty()

                binding.apply {
                    if (isEmpty) {
                        tvTodayTitleMessage.text =
                            getString(R.string.today_lesson_title_not_register)
                        ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_not_start)
                    } else if (isNotStart) {
                        tvTodayTitleMessage.text =
                            getString(R.string.today_lesson_title_not_start)
                        ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_not_start)
                    } else if (isFinished) {
                        tvTodayTitleMessage.text =
                            getString(R.string.today_lesson_title_win)
                        ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_lose)
                    } else {
                        tvTodayTitleMessage.text =
                            getString(R.string.today_lesson_title_lose)
                        ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_win)
                    }
                }
            } else {
                val isDoing =
                    onBoardingDoingList.value.isNotEmpty() && onBoardingFinishedList.value.isEmpty()
                val isFinished =
                    onBoardingDoingList.value.isEmpty() && onBoardingFinishedList.value.isNotEmpty()
                binding.apply {
                    if (isDoing) {
                        tvTodayTitleMessage.text =
                            getString(R.string.today_lesson_title_lose_onboarding)
                        ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_win)
                    } else if (isFinished) {
                        tvTodayTitleMessage.text =
                            getString(R.string.today_lesson_title_win_onboarding)
                        ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_lose_onboarding)
                    }
                }
            }
            binding.rvTodayLesson.apply {
                // header 와 첫번째 아이템 사이의 간격 조정
                if (itemDecorationCount == 0)
                    addItemDecoration(LessonItemDecoration(requireContext(), 16))
            }
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
                autoLoginEvent.collect { loginState ->
                    when (loginState) {
                        true -> Unit
                        false -> navigateToLogin()
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

//            launch {
//                showOnBoardingClickPlusEvent.collect {
//                    setOnBoardingList()
//                    val action =
//                        TodayLessonFragmentDirections.actionTodayLessonToOnBoardingClickPlusDialog()
//                    findNavController().safeNavigate(action)
//                }
//            }

//            launch {
//                onBoardingDoingList.collect {
//                    doingOnBoardingAdapter.submitList(it)
//                }
//            }
//
//            launch {
//                onBoardingFinishedList.collect {
//                    finishedOnBoardingAdapter.submitList(it)
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
                fetchLessonListSuccessEvent
                    .collect {
                        initViews()
                        initAdapter()
                    }
            }

            launch {
                lessonEmptyList.collect {
                    emptyLessonAdapter.submitList(it)
                }
            }

            launch {
                lessonDoingList.collect {
                    finishedLessonAdapter.submitList(it)
                }
            }

            launch {
                lessonFinishedList.collect {
                    doingLessonAdapter.submitList(it)
                }
            }

            launch {
                navigateToWaitDialogEvent.collect {
                    showWaitDialog()
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
                navigateRegisterLessonEvent
                    .collect {
                        val action = TodayLessonFragmentDirections.actionTodayLessonToRegisterLessonFirst()
                        findNavController().safeNavigate(action)
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

    private fun initAdapter() = with(todayLessonViewModel) {
        val isOnBoardingComplete = onBoardingComplete.value

        if (isOnBoardingComplete) {
            val isEmpty = lessonEmptyList.value.isNotEmpty()
            if (isEmpty) {
                concatAdapter = ConcatAdapter(
                    emptyHeader,
                    emptyLessonAdapter
                )
                binding.rvTodayLesson.adapter = concatAdapter
            } else {
                concatAdapter = ConcatAdapter(
                    doingHeader,
                    doingLessonAdapter,
                    finishedHeader,
                    finishedLessonAdapter
                )
                if (lessonDoingList.value.isEmpty()) {
                    concatAdapter.removeAdapter(doingHeader)
                    concatAdapter.removeAdapter(doingLessonAdapter)
                } else if (lessonFinishedList.value.isEmpty()) {
                    concatAdapter.removeAdapter(finishedHeader)
                    concatAdapter.removeAdapter(finishedLessonAdapter)
                }
                binding.rvTodayLesson.adapter = concatAdapter
            }
        } else {
            concatAdapter = ConcatAdapter(
                doingHeader,
                doingOnBoardingAdapter,
                finishedHeader,
                finishedOnBoardingAdapter
            )
            if (onBoardingDoingList.value.isEmpty()) {
                concatAdapter.removeAdapter(doingHeader)
                concatAdapter.removeAdapter(doingOnBoardingAdapter)
            } else if (onBoardingFinishedList.value.isEmpty()) {
                concatAdapter.removeAdapter(finishedHeader)
                concatAdapter.removeAdapter(finishedOnBoardingAdapter)
            }
            binding.rvTodayLesson.adapter = concatAdapter
        }
    }

    private fun navigateToLogin() {
        val action =
            TodayLessonFragmentDirections.actionTodayLessonToLogin()
        findNavController().safeNavigate(action)
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