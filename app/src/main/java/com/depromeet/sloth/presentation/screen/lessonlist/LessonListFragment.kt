package com.depromeet.sloth.presentation.screen.lessonlist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLessonListBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.LessonListAdapter
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

//TODO 강의 등록 popUp 설정 조건부로 설정해야함
@AndroidEntryPoint
class LessonListFragment : BaseFragment<FragmentLessonListBinding>(R.layout.fragment_lesson_list) {

    private val lessonListViewModel: LessonListViewModel by viewModels()

    private val lessonListItemClickListener = LessonListItemClickListener(
        onRegisterClick = { lessonListViewModel.navigateToRegisterLesson() },
        onLessonClick = { lesson -> lessonListViewModel.navigateToLessonDetail(lesson.lessonId.toString()) }
    )

    private val lessonListAdapter by lazy {
        LessonListAdapter(lessonListItemClickListener)
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
            launch {
                checkLessonListOnBoardingCompleteEvent.collect { isOnBoardingComplete ->
                        if (!isOnBoardingComplete) {
                            navigateToOnBoardingCheckDetail()
                        }
                    }
            }

            launch {
                navigateToOnBoardingCheckDetailEvent.collect {
                    showOnBoardingCheckDetail()
                }
            }

            launch {
                lessonList.collect {
                    lessonListAdapter.submitList(it)
                }
            }

            launch {
                navigateRegisterLessonEvent.collect {
                    val action = LessonListFragmentDirections.actionLessonListToRegisterLessonFirst()
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                navigateToLessonDetailEvent.collect { lessonId ->
                    val action = LessonListFragmentDirections.actionLessonListToLessonDetail(lessonId)
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                navigateToNotificationListEvent.collect {
                    showWaitDialog()
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

    private fun showOnBoardingCheckDetail() {
        val action = LessonListFragmentDirections.actionLessonListToOnBoardingCheckDetailDialog()
        findNavController().safeNavigate(action)
    }

    private fun showWaitDialog() {
        val action = LessonListFragmentDirections.actionLessonListToWaitDialog()
        findNavController().safeNavigate(action)
    }

}