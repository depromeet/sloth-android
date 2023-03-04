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
import timber.log.Timber


@AndroidEntryPoint
class LessonListFragment : BaseFragment<FragmentLessonListBinding>(R.layout.fragment_lesson_list) {

    private val viewModel: LessonListViewModel by viewModels()

    private val lessonListItemClickListener = LessonListItemClickListener(
        onRegisterClick = { viewModel.navigateToRegisterLesson(R.id.lesson_list) },
        onLessonClick = { lesson -> viewModel.navigateToLessonDetail(lesson.lessonId.toString()) }
    )

    private val lessonListAdapter by lazy {
        LessonListAdapter(lessonListItemClickListener)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchLessonList()
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
        binding.rvLessonList.apply {
            if (itemDecorationCount == 0) {
                addItemDecoration(LessonItemDecoration(requireContext(), 16))
            }
            adapter = lessonListAdapter
        }
    }

    private fun initListener() {
        binding.tbLessonList.setOnMenuItemSingleClickListener {
            when (it.itemId) {
                R.id.menu_register_lesson -> {
                    viewModel.navigateToRegisterLesson(R.id.lesson_list)
                    true
                }
                R.id.menu_notification_list -> {
                    viewModel.navigateToNotificationList()
                    true
                }
                else -> false
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.checkLessonListOnBoardingCompleteEvent.collect { isOnBoardingComplete ->
                    if (!isOnBoardingComplete) {
                        Timber.d("온보딩 완료 안됨")
                        viewModel.navigateToOnBoardingCheckDetail()
                    } else {
                        Timber.d("온보딩 완료됨")
                    }
                }
            }

            launch {
                viewModel.navigateToOnBoardingCheckDetailEvent.collect {
                    showOnBoardingCheckDetail()
                }
            }

            launch {
                viewModel.lessonList.collect {
                    lessonListAdapter.submitList(it)
                }
            }

            launch {
                viewModel.navigateRegisterLessonEvent.collect { fragmentId ->
                    val action = LessonListFragmentDirections.actionLessonListToRegisterLessonFirst(fragmentId)
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                viewModel.navigateToLessonDetailEvent.collect { lessonId ->
                    val action = LessonListFragmentDirections.actionLessonListToLessonDetail(lessonId)
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                viewModel.navigateToNotificationListEvent.collect {
                    showWaitDialog()
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


    private fun showOnBoardingCheckDetail() {
        val action = LessonListFragmentDirections.actionLessonListToOnBoardingCheckDetailDialog()
        findNavController().safeNavigate(action)
    }

    private fun showWaitDialog() {
        val action = LessonListFragmentDirections.actionLessonListToWaitDialog()
        findNavController().safeNavigate(action)
    }
}