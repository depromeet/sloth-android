package com.depromeet.sloth.presentation.screen.todaylesson

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.databinding.FragmentTodayLessonBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.HeaderAdapter
import com.depromeet.sloth.presentation.adapter.TodayLessonAdapter
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.UNAUTHORIZED
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val todayLessonViewModel: TodayLessonViewModel by viewModels()

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
            if (itemDecorationCount == 0)
                addItemDecoration(LessonItemDecoration(requireContext(), 16))
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
                autoLoginEvent
                    .collect { loginState ->
                        when (loginState) {
                            true -> fetchTodayLessonList()
                            else -> navigateToLogin()
                        }
                    }
            }

            launch {
                fetchLessonListSuccessEvent
                    .collect {
                        setLessonList(it)
                    }
            }

            launch {
                navigateToWaitDialogEvent
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
        val action = TodayLessonFragmentDirections.actionTodayLessonToFinishLessonDialog(lessonId)
        findNavController().safeNavigate(action)
    }

    private fun setLessonList(lessonTodayList: List<LessonTodayResponse>) {
        when (lessonTodayList.isEmpty()) {
            true -> {
                val emptyHeader = HeaderAdapter(HeaderAdapter.HeaderType.EMPTY)
                val emptyLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.EMPTY) { _, _, _ ->
                        val action =
                            TodayLessonFragmentDirections.actionTodayLessonToRegisterLessonFirst()
                        findNavController().safeNavigate(action)
                    }
                val concatAdapter = ConcatAdapter(
                    emptyHeader,
                    emptyLessonAdapter
                )
                emptyLessonAdapter.submitList(listOf(LessonTodayResponse.EMPTY))

                binding.apply {
                    rvTodayLesson.adapter = concatAdapter
                    tvTodayTitleMessage.text = getString(R.string.home_today_title_not_register)
                    ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_not_start)
                }
            }

            false -> {
                val lessonFinishedList = mutableListOf<LessonTodayResponse>()
                val lessonNotFinishedList = mutableListOf<LessonTodayResponse>()
                lessonTodayList.forEach { lesson ->
                    if (lesson.untilTodayFinished) lessonFinishedList.add(lesson)
                    else lessonNotFinishedList.add(lesson)
                }

                val notFinishedHeader =
                    HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)

                val notFinishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED) { clickType, lessonToday, delayTime ->
                        when (clickType) {
                            TodayLessonAdapter.ClickType.CLICK_PLUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS.value,
                                    TodayLessonAdapter.BodyType.NOT_FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS,
                                    delayTime
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_MINUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS.value,
                                    TodayLessonAdapter.BodyType.NOT_FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS,
                                    delayTime
                                )
                            }

                            else -> Unit
                        }
                    }
                val finishedHeader =
                    HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)

                val finishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED) { clickType, lessonToday, delayTime ->
                        when (clickType) {
                            TodayLessonAdapter.ClickType.CLICK_PLUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS.value,
                                    TodayLessonAdapter.BodyType.FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS,
                                    delayTime
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_MINUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS.value,
                                    TodayLessonAdapter.BodyType.FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS,
                                    delayTime
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_COMPLETE -> {
                                showFinishLessonDialog(lessonToday.lessonId.toString())
                            }

                            else -> {}
                        }
                    }
                val concatAdapter = ConcatAdapter(
                    notFinishedHeader,
                    notFinishedLessonAdapter,
                    finishedHeader,
                    finishedLessonAdapter
                )

                if (lessonFinishedList.isEmpty()) {
                    concatAdapter.removeAdapter(finishedHeader)
                    concatAdapter.removeAdapter(finishedLessonAdapter)
                } else {
                    finishedLessonAdapter.submitList(lessonFinishedList)
                }

                if (lessonNotFinishedList.isEmpty()) {
                    concatAdapter.removeAdapter(notFinishedHeader)
                    concatAdapter.removeAdapter(notFinishedLessonAdapter)
                } else {
                    notFinishedLessonAdapter.submitList(lessonNotFinishedList)
                }

                binding.apply {
                    rvTodayLesson.adapter = concatAdapter
                    when {
                        lessonFinishedList.isNotEmpty() && lessonNotFinishedList.isEmpty() -> {
                            tvTodayTitleMessage.text =
                                getString(R.string.home_today_title_win)
                            ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_lose)
                        }

                        lessonFinishedList.isEmpty() && (lessonNotFinishedList.any { it.presentNumber > 0 }
                            .not()) -> {
                            tvTodayTitleMessage.text =
                                getString(R.string.home_today_title_not_start)
                            ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_not_start)
                        }

                        else -> {
                            tvTodayTitleMessage.text =
                                getString(R.string.home_today_title_lose)
                            ivTodaySloth.setImageResource(R.drawable.ic_today_lesson_sloth_win)
                        }
                    }
                }
            }
        }
    }

    //TODO Flow 로 변경
    //TODO 다른 api 호출 로직과 같은 방식으로 변경
    private fun updateLessonCount(
        lesson: LessonTodayResponse,
        count: Int,
        bodyType: TodayLessonAdapter.BodyType,
        clickType: TodayLessonAdapter.ClickType,
        delayTime: Long
    ) {
        mainScope {
            todayLessonViewModel.updateLessonCount(count, lesson.lessonId).let { result ->
                when (result) {
                    is Result.Loading -> showProgress()
                    is Result.Success<LessonUpdateCountResponse> -> {
                        delay(delayTime)
                        when (bodyType) {
                            TodayLessonAdapter.BodyType.NOT_FINISHED -> {
                                if (result.data.presentNumber == lesson.untilTodayNumber
                                //it.data.presentNumber == 0 || (clickType == TodayLessonAdapter.ClickType.CLICK_PLUS && it.data.presentNumber == 1)
                                )
                                    todayLessonViewModel.fetchTodayLessonList()
                            }

                            TodayLessonAdapter.BodyType.FINISHED -> {
                                if (result.data.presentNumber < lesson.untilTodayNumber ||
                                    result.data.presentNumber == lesson.totalNumber ||
                                    result.data.presentNumber + 1 == lesson.totalNumber && (clickType == TodayLessonAdapter.ClickType.CLICK_MINUS)
                                )
                                    todayLessonViewModel.fetchTodayLessonList()
                            }

                            else -> Unit
                        }
                    }

                    is Result.Error -> {
                        if (result.throwable.message == INTERNET_CONNECTION_ERROR) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.lesson_update_fail_by_internet_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (result.statusCode == UNAUTHORIZED) {
                            showExpireDialog()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.lesson_update_fail),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            hideProgress()
        }
    }

}