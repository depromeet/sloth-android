package com.depromeet.sloth.presentation.screen.lessonlist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.FragmentLessonListBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.LessonListAdapter
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LessonListFragment : BaseFragment<FragmentLessonListBinding>(R.layout.fragment_lesson_list) {

    private val lessonListViewModel: LessonListViewModel by viewModels()

    private val lessonListAdapter by lazy {
        LessonListAdapter { lesson ->
            when (lesson) {
                LessonListResponse.EMPTY -> lessonListViewModel.navigateToRegisterLesson()
                else -> lessonListViewModel.navigateToLessonDetail(lesson.lessonId.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lessonListViewModel.fetchLessonList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = lessonListViewModel
        }
        initViews()
        initListener()
        initObserver()
    }

    override fun initViews() {
        binding.rvLessonList.apply {
            if (itemDecorationCount == 0) {
                addItemDecoration(LessonItemDecoration(requireContext(), 16))
            }
            adapter = lessonListAdapter
        }
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
            // TODO 투데이 화면에서 온보딩 안끝났으면 아예 강의 목록 화면으로 넘어오지 못하도록 로직 처리
            //  check 를 통해 온보딩 로직이 끝나지 않았다면 빠꾸
            //  근데 그러면 checkDetail OnBoarding 화면을 띄울 수 없음 -> onBoardingStatus 를 분리
//            launch {
//                checkOnBoardingCompleteEvent
//                    .collect { isOnBoardingComplete ->
//                        if (!isOnBoardingComplete) {
//                            val action = LessonListFragmentDirections.actionLessonListToTodayLesson()
//                            findNavController().safeNavigate(action)
//                        }
//                    }
//
//                    .collect { isCompleteOnBoarding ->
//                        when (isCompleteOnBoarding) {
//                            true -> Unit
//                            false -> showOnBoardingCheckDetail()
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
                lessonList.collect {
                    lessonListAdapter.submitList(it)
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
                    .collect { lessonId ->
                        val action =
                            LessonListFragmentDirections.actionLessonListToLessonDetail(lessonId)
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

    private fun showWaitDialog() {
        val action = LessonListFragmentDirections.actionLessonListToWaitDialog()
        findNavController().safeNavigate(action)
    }
}