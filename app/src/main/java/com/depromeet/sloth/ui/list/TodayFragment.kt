package com.depromeet.sloth.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.*
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
//        mainScope {
//            showProgress()
//            binding.ivTodaySloth.visibility = View.INVISIBLE
//
//            viewModel.fetchTodayLessonList().let {
//                when (it) {
//                    is LessonState.Loading -> handleLoadingState(requireContext())
//                    is LessonState.Success<List<LessonTodayResponse>> -> {
//                        val lessonTodayList = it.data
//                        setLessonList(lessonTodayList)
//                    }
//                    is LessonState.Error -> {
//
//                    }
//                    else -> Unit
//                }
//                hideProgress()
//            }
//
//            binding.ivTodaySloth.visibility = View.VISIBLE
//            hideProgress()
//        }
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
                        is UIState.Unauthorized -> showToast("?????? ????????? ????????????")
                        is UIState.Error -> showToast("?????? ????????? ???????????? ????????????")
                    }
                }
        }
    }

    private fun moveRegisterActivity() {
        startActivity(Intent(requireActivity(), RegisterLessonActivity::class.java))
    }

    private fun moveDetailActivity(lessonToday: LessonTodayResponse) {
        val intent = Intent(requireContext(), LessonDetailActivity::class.java)
        intent.putExtra("lessonId", lessonToday.lessonId.toString())
        startActivity(intent)
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
                                    it.data.presentNumber + 1 == lesson.totalNumber && (clickType == TodayLessonAdapter.ClickType.CLICK_MINUS)) fetchLessonList() else Unit
                            }
                            else -> Unit
                        }
                    }
                    is LessonState.Error -> {
                        showToast("?????? ????????? ???????????? ?????? ????????????")
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
                            showToast("?????? ????????? ???????????? ????????????")
                        }
                        is UIState.Unauthorized -> showToast("?????? ????????? ????????????")
                        is UIState.Error -> showToast("?????? ??????????????? ??????????????????")
                    }
                }
        }
    }

    private fun setTestData() {
        val dummyList = listOf<LessonTodayResponse>(
            LessonTodayResponse(
                categoryName = "??????",
                lessonId = 1,
                lessonName = "??????????????? ???????????? : \n????????? ?????? (Inflearn Original)",
                presentNumber = 4,
                remainDay = 9,
                siteName = "TEST1",
                untilTodayFinished = false,
                untilTodayNumber = 8,
                totalNumber = 8
            ),
            LessonTodayResponse(
                categoryName = "?????????",
                lessonId = 2,
                lessonName = "??????????????? ???????????? : \n????????? ?????? (Inflearn Original)",
                presentNumber = 5,
                remainDay = 19,
                siteName = "TEST2",
                untilTodayFinished = true,
                untilTodayNumber = 5,
                totalNumber = 8
            ),
            LessonTodayResponse(
                categoryName = "??????",
                lessonId = 3,
                lessonName = "??????????????? ???????????? : \n????????? ?????? (Inflearn Original)",
                presentNumber = 4,
                remainDay = 10,
                siteName = "TEST3",
                untilTodayFinished = false,
                untilTodayNumber = 6,
                totalNumber = 8
            ),
            LessonTodayResponse(
                categoryName = "??????",
                lessonId = 4,
                lessonName = "??????????????? ???????????? : \n????????? ?????? (Inflearn Original)",
                presentNumber = 6,
                remainDay = 7,
                siteName = "TEST4",
                untilTodayFinished = true,
                untilTodayNumber = 6,
                totalNumber = 8
            ),
            LessonTodayResponse(
                categoryName = "?????????",
                lessonId = 5,
                lessonName = "??????????????? ???????????? : \n????????? ?????? (Inflearn Original)",
                presentNumber = 1,
                remainDay = 11,
                siteName = "TEST5",
                untilTodayFinished = false,
                untilTodayNumber = 4,
                totalNumber = 8
            ),
            LessonTodayResponse(
                categoryName = "??????",
                lessonId = 6,
                lessonName = "??????????????? ???????????? : \n????????? ?????? (Inflearn Original)",
                presentNumber = 2,
                remainDay = 1,
                siteName = "TEST6",
                untilTodayFinished = false,
                untilTodayNumber = 3,
                totalNumber = 8
            )
        )

        val notFinishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
        val finishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)
        val notFinishedLessonAdapter =
            TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED) { clickType, _ ->
                when (clickType) {
                    TodayLessonAdapter.ClickType.CLICK_PLUS -> {

                    }

                    TodayLessonAdapter.ClickType.CLICK_MINUS -> {

                    }

                    TodayLessonAdapter.ClickType.CLICK_NORMAL -> {

                    }
                }
            }
        val finishedLessonAdapter =
            TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED) { _, lesson ->
                moveDetailActivity(lesson)
            }
        val concatAdapter = ConcatAdapter(
            notFinishedHeader,
            notFinishedLessonAdapter,
            finishedHeader,
            finishedLessonAdapter
        )

        dummyList.let {
            finishedLessonAdapter.submitList(
                dummyList.filter { it.untilTodayFinished }
            )
            notFinishedLessonAdapter.submitList(
                dummyList.filter { it.untilTodayFinished.not() }
            )
        }

        binding.rvTodayLesson.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }
}
