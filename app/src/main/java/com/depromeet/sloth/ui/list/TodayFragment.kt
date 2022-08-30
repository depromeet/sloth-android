package com.depromeet.sloth.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountResponse
import com.depromeet.sloth.databinding.FragmentTodayBinding
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.base.UIState
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.LessonItemDecoration
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.detail.LessonDetailActivity
import com.depromeet.sloth.ui.register.RegisterLessonActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class TodayFragment : BaseFragment<FragmentTodayBinding>(R.layout.fragment_today) {

    private val lessonViewModel: LessonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        fetchLessonList()
    }

    override fun initViews() {
        with(binding) {
            rvTodayLesson.addItemDecoration(LessonItemDecoration(requireContext(), 16))

            ivTodayAlarm.setOnClickListener {
                val dlg = SlothDialog(requireContext(), DialogState.WAIT)
                dlg.start()
            }
        }
    }

    private fun fetchLessonList() {
        viewLifecycleOwner.lifecycleScope.launch {
            lessonViewModel.todayLessonList
                .onStart { binding.ivTodaySloth.visibility = View.INVISIBLE }
                .onCompletion { binding.ivTodaySloth.visibility = View.VISIBLE }
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        is UIState.Loading -> showProgress()
                        is UIState.UnLoading -> hideProgress()
                        is UIState.Success -> setLessonList(uiState.data)
                        is UIState.Unauthorized -> showToast("다시 로그인 해주세요")
                        is UIState.Error -> showToast("강의 정보를 가져오지 못했어요")
                    }
                }
        }
    }

    private fun moveRegisterActivity() {
        startActivity(Intent(requireActivity(), RegisterLessonActivity::class.java))
    }

    private fun setLessonList(lessonTodayList: List<LessonTodayResponse>) {
        when (lessonTodayList.isEmpty()) {
            true -> {
                val nothingHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOTHING)
                val nothingLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOTHING) { _, _ -> moveRegisterActivity() }
                val concatAdapter = ConcatAdapter(
                    nothingHeader,
                    nothingLessonAdapter
                )
                nothingLessonAdapter.submitList(listOf(LessonTodayResponse.EMPTY))

                binding.apply {
                    rvTodayLesson.adapter = concatAdapter
                    tvTodayTitleMessage.text = getString(R.string.home_today_title_not_register)
                }
            }

            false -> {
                Timber.d("lessonTodayList: $lessonTodayList")
                val lessonFinishedList = mutableListOf<LessonTodayResponse>()
                val lessonNotFinishedList = mutableListOf<LessonTodayResponse>()
                lessonTodayList.forEach { lesson ->
                    if (lesson.untilTodayFinished) lessonFinishedList.add(lesson)
                    else lessonNotFinishedList.add(lesson)
                }

                val notFinishedHeader =
                    HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
                val notFinishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED) { clickType, lessonToday ->
                        when (clickType) {
                            TodayLessonAdapter.ClickType.CLICK_PLUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS.value,
                                    TodayLessonAdapter.BodyType.NOT_FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_MINUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS.value,
                                    TodayLessonAdapter.BodyType.NOT_FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS
                                )
                            }
                        }
                    }
                val finishedHeader =
                    HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)
                val finishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED) { clickType, lessonToday ->
                        when (clickType) {
                            TodayLessonAdapter.ClickType.CLICK_PLUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS.value,
                                    TodayLessonAdapter.BodyType.FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_PLUS
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_MINUS -> {
                                updateLessonCount(
                                    lessonToday,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS.value,
                                    TodayLessonAdapter.BodyType.FINISHED,
                                    TodayLessonAdapter.ClickType.CLICK_MINUS
                                )
                            }

                            TodayLessonAdapter.ClickType.CLICK_COMPLETE -> {
                                showCompleteDialog(lessonToday.lessonId.toString())
                            }
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
                            ivTodaySloth.setImageResource(R.drawable.ic_home_today_sloth_lose)
                        }
                        lessonFinishedList.isEmpty() && (lessonNotFinishedList.any { it.presentNumber > 0 }
                            .not()) -> {
                            tvTodayTitleMessage.text =
                                getString(R.string.home_today_title_not_start)
                            ivTodaySloth.setImageResource(R.drawable.ic_home_today_sloth_not_start)
                        }
                        else -> {
                            tvTodayTitleMessage.text =
                                getString(R.string.home_today_title_lose)
                            ivTodaySloth.setImageResource(R.drawable.ic_home_today_sloth_win)
                        }
                    }
                }
            }
        }
    }

    private fun updateLessonCount(
        lesson: LessonTodayResponse,
        count: Int,
        bodyType: TodayLessonAdapter.BodyType,
        clickType: TodayLessonAdapter.ClickType,
    ) {
        mainScope {
            showProgress()

            lessonViewModel.updateLessonCount(count, lesson.lessonId).let {
                when (it) {
                    is LessonState.Loading -> showProgress()
                    is LessonState.Success<LessonUpdateCountResponse> -> {
                        when (bodyType) {
                            TodayLessonAdapter.BodyType.NOT_FINISHED -> {
                                if (it.data.presentNumber == lesson.untilTodayNumber ||
                                    it.data.presentNumber == 0 || (clickType == TodayLessonAdapter.ClickType.CLICK_PLUS && it.data.presentNumber == 1)
                                ) fetchLessonList() else Unit
                            }
                            TodayLessonAdapter.BodyType.FINISHED -> {
                                if (it.data.presentNumber < lesson.untilTodayNumber ||
                                    it.data.presentNumber == lesson.totalNumber ||
                                    it.data.presentNumber + 1 == lesson.totalNumber && (clickType == TodayLessonAdapter.ClickType.CLICK_MINUS)
                                ) fetchLessonList() else Unit
                            }
                            else -> Unit
                        }
                    }
                    is LessonState.Error -> {
                        showToast("강의 정보를 업데이트 하지 못했어요")
                        Timber.tag("Error").d(it.throwable)
                    }
                    else -> Unit
                }
            }

            hideProgress()
        }
    }

    private fun showCompleteDialog(lessonId: String) {
        val dlg = SlothDialog(requireContext(), DialogState.COMPLETE)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                finishLesson(lessonId)
            }
        }
        dlg.start()
    }

    private fun finishLesson(lessonId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            lessonViewModel.finishLesson(lessonId)
                .onStart { binding.ivTodaySloth.visibility = View.INVISIBLE }
                .onCompletion { binding.ivTodaySloth.visibility = View.VISIBLE }
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { uiState ->
                    when (uiState) {
                        is UIState.Loading -> showProgress()
                        is UIState.UnLoading -> hideProgress()
                        is UIState.Success -> {
                            fetchLessonList()
                            showToast("해당 강의가 완료처리 되었어요")
                        }
                        is UIState.Unauthorized -> showToast("다시 로그인 해주세요")
                        is UIState.Error -> showToast("강의 완료처리에 실패하였어요")
                    }
                }
        }
    }
}
