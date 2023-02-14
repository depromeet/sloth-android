package com.depromeet.sloth.presentation.screen.lessonlist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLessonListBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.HeaderAdapter
import com.depromeet.sloth.presentation.adapter.LessonListAdapter
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


//TODO Empty일때 핸들링
@AndroidEntryPoint
class LessonListFragment : BaseFragment<FragmentLessonListBinding>(R.layout.fragment_lesson_list) {

    private val lessonListViewModel: LessonListViewModel by viewModels()

    private lateinit var concatAdapter: ConcatAdapter

    private val emptyLessonAdapter by lazy {
        LessonListAdapter {
            lessonListViewModel.navigateToRegisterLesson()
        }
    }

    private val currentHeader by lazy {
        HeaderAdapter(
            HeaderAdapter.HeaderType.CURRENT,
            lessonListViewModel.currentLessonList.value.size
        )
    }

    private val currentLessonAdapter by lazy {
        LessonListAdapter { lesson ->
            lessonListViewModel.navigateToLessonDetail(lesson)
        }
    }

    private val planHeader by lazy {
        HeaderAdapter(
            HeaderAdapter.HeaderType.PLAN,
            lessonListViewModel.planLessonList.value.size
        )
    }

    private val planLessonAdapter by lazy {
        LessonListAdapter { lesson ->
            lessonListViewModel.navigateToLessonDetail(lesson)
        }
    }

    private val pastHeader by lazy {
        HeaderAdapter(
            HeaderAdapter.HeaderType.PAST,
            lessonListViewModel.pastLessonList.value.size
        )
    }

    private val pastLessonAdapter by lazy {
        LessonListAdapter { lesson ->
            lessonListViewModel.navigateToLessonDetail(lesson)
        }
    }

    override fun onStart() {
        super.onStart()
        lessonListViewModel.fetchLessonList()
        // TODO fetchLessonList 내부로 이동
        // lessonListViewModel.checkOnBoardingComplete()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = lessonListViewModel
        }
        initListener()
        initObserver()
    }

    private fun initListener() = with(binding) {
        tbLessonList.apply {
            setOnMenuItemSingleClickListener {
                when (it.itemId) {
                    R.id.menu_register_lesson -> {
                        lessonListViewModel.navigateToRegisterLesson()
                        true
                    }
                    R.id.menu_notification_list -> {
                        lessonListViewModel.navigateToNotificationList()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initObserver() = with(lessonListViewModel) {
        repeatOnStarted {
            // TODO 투데이 화면에서 온보딩 안끝났으면 아예 강의 목록 화면으로 넘어오지 못하게 해야함
            // check 해서 다시 돌아가게 빠꾸 먹일까 그게 쉬울 것 같은데
            // 근데 그러면 checkDetail OnBoarding 화면을 띄울 수 없음
            // onBoarding 이 Complete 되었을 때 띄운다면 매번 강의 목록 화면에 진입 할때마다 checkDetail 온보딩이 수행됨
//            launch {
//                checkOnBoardingCompleteEvent
//                    .collect { isOnBoardingComplete ->
//                        if (!isOnBoardingComplete) {
//                            val action = ManageFragmentDirections.actionManageToTodayLesson()
//                            findNavController().safeNavigate(action)
//                        }
//                    }

//                    .collect { isCompleteOnBoarding ->
//                        when (isCompleteOnBoarding) {
//                            true -> Unit
//                            false -> {
//                                showOnBoardingCheckDetail()
//                            }
//                        }
//                    }
//            }
//
//            launch {
//                showOnBoardingCheckDetailEvent
//                    .collect {
//                        val action =
//                            LessonListFragmentDirections.actionLessonListToOnBoardingCheckDetailDialog()
//                        findNavController().safeNavigate(action)
//                    }
//            }

            launch {
                fetchLessonListSuccessEvent
                    .collect {
                        initViews()
                        initAdapter()
                    }
            }

            launch {
                emptyLessonList.collect {
                    emptyLessonAdapter.submitList(it)
                }
            }

            launch {
                currentLessonList.collect {
                    currentLessonAdapter.submitList(it)
                }
            }

            launch {
                planLessonList.collect {
                    planLessonAdapter.submitList(it)
                }
            }

            launch {
                pastLessonList.collect {
                    pastLessonAdapter.submitList(it)
                }
            }

            launch {
                navigateRegisterLessonEvent
                    .collect {
                        val action =
                            LessonListFragmentDirections.actionLessonListToRegisterLessonFirst()
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                navigateToLessonDetailEvent
                    .collect { lesson ->
                        val action =
                            LessonListFragmentDirections.actionLessonListToLessonDetail(lesson.lessonId.toString())
                        findNavController().safeNavigate(action)
                    }
            }

            launch {
                navigateToNotificationListEvent
                    .collect {
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

    override fun initViews() {
        val isEmpty = lessonListViewModel.emptyLessonList.value.isNotEmpty()
        binding.apply {
            tbLessonList.menu.findItem(R.id.menu_register_lesson).isVisible = !isEmpty

            if (rvLessonList.itemDecorationCount == 0) {
                rvLessonList.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            }
        }
    }

    private fun initAdapter() = with(lessonListViewModel) {
        if (emptyLessonList.value.isNotEmpty()) {
            binding.rvLessonList.adapter = emptyLessonAdapter
        } else {
            concatAdapter = ConcatAdapter(
                currentHeader,
                currentLessonAdapter,
                planHeader,
                planLessonAdapter,
                pastHeader,
                pastLessonAdapter
            )
            if (currentLessonList.value.isEmpty()) {
                concatAdapter.removeAdapter(currentHeader)
                concatAdapter.removeAdapter(currentLessonAdapter)
            } else if (planLessonList.value.isEmpty()) {
                concatAdapter.removeAdapter(planHeader)
                concatAdapter.removeAdapter(planLessonAdapter)
            } else if (pastLessonList.value.isEmpty()) {
                concatAdapter.removeAdapter(pastHeader)
                concatAdapter.removeAdapter(pastLessonAdapter)
            }
            binding.rvLessonList.adapter = concatAdapter
        }
    }

    private fun showWaitDialog() {
        val action = LessonListFragmentDirections.actionLessonListToWaitDialog()
        findNavController().safeNavigate(action)
    }
}