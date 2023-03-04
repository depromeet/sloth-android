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
import timber.log.Timber


// TODO 로그가 2의 배수 번 호출되는 이슈 뭐지...???
@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val viewModel: TodayLessonViewModel by viewModels()

    private val todayLessonItemClickListener = TodayLessonItemClickListener(
        onClick = { viewModel.navigateToRegisterLesson(R.id.today_lesson) },
        onPlusClick = { lesson -> viewModel.updateLessonCount(1, lesson) },
        // onPlusClick = { viewModel.updateLessonCount(1) },
        onMinusClick = { lesson -> viewModel.updateLessonCount(-1, lesson) },
        // onMinusClick = { viewModel.updateLessonCount(-1) },
        onFinishClick = { lesson -> viewModel.navigateToFinishLessonDialog(lesson.lessonId.toString()) }
    )

//    override fun onStart() {
//        super.onStart()
//        viewModel.fetchTodayLessonList()
//    }

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
                    viewModel.navigateToWaitDialog()
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
                    if (!isLoggedIn) {
                        val action = TodayLessonFragmentDirections.actionTodayLessonToLogin()
                        findNavController().safeNavigate(action)
                    } else {
                        Timber.d("자동 로그인")
                        viewModel.checkTodayLessonOnBoardingComplete()
                    }
                }
            }
            //TODO 한번 다른화면 갔다가 오면 호출이 2배씩 늘어남
            // api 는 중복 호출을 막아놔서 한번씩만 호출되긴 함
            launch {
                viewModel.checkTodayLessonOnBoardingCompleteEvent.collect { isOnBoardingComplete ->
                    if (!isOnBoardingComplete) {
                        Timber.d("온보딩 완료 안됨")
                        val action = TodayLessonFragmentDirections.actionTodayLessonToOnBoardingTodayLesson()
                        findNavController().safeNavigate(action)
                    } else {
                        Timber.d("온보딩 완료")
                        viewModel.fetchTodayLessonList()
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
                viewModel.navigateToTodayLessonOnBoardingEvent.collect {
                    val action = TodayLessonFragmentDirections.actionTodayLessonToOnBoardingTodayLesson()
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
                    showWaitDialog()
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