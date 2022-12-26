package com.depromeet.sloth.ui.today

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.databinding.FragmentTodayLessonBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.adapter.HeaderAdapter
import com.depromeet.sloth.ui.adapter.TodayLessonAdapter
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.LessonItemDecoration
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class TodayLessonFragment :
    BaseFragment<FragmentTodayLessonBinding>(R.layout.fragment_today_lesson) {

    private val todayLessonViewModel: TodayLessonViewModel by viewModels()

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
            if (itemDecorationCount == 0)
                addItemDecoration(LessonItemDecoration(requireContext(), 16))
        }
    }

    private fun initListener() = with(binding) {
        itemNetworkError.btnRetry.setOnClickListener {
            todayLessonViewModel.fetchTodayLessonList()
        }

        tbTodayLesson.apply {
            setOnMenuItemSingleClickListener {
                when (it.itemId) {
                    R.id.menu_notification_list -> {
                        todayLessonViewModel.navigateToNotificationList()
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
                fetchTodayLessonListEvent
//                .onStart { binding.ivTodaySloth.visibility = View.INVISIBLE }
//                .onCompletion { binding.ivTodaySloth.visibility = View.VISIBLE }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success -> {
                                binding.itemNetworkError.itemNetworkError.visibility =
                                    View.GONE
                                setLessonList(result.data)
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(
                                        requireContext(),
                                        this@TodayLessonFragment
                                    ) {
                                        todayLessonViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(
                                            requireContext(),
                                            getString(R.string.lesson_info_fetch_fail)
                                        )
                                        binding.itemNetworkError.itemNetworkError.visibility =
                                            View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                finishLessonEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success -> {
                                showToast(
                                    requireContext(),
                                    getString(R.string.lesson_finish_complete)
                                )
                                todayLessonViewModel.fetchTodayLessonList()
                            }

                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(
                                        requireContext(),
                                        this@TodayLessonFragment
                                    ) {
                                        todayLessonViewModel.removeAuthToken()
                                    }

                                    else -> {
                                        Timber.tag("Finish Error").d(result.throwable)
                                        showToast(
                                            requireContext(), getString(
                                                R.string.lesson_finish_fail
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                navigateToNotificationListEvent
                    .collect {
                        showWaitDialog(requireContext())
                    }
            }
        }
    }

    private fun setLessonList(lessonTodayList: List<LessonTodayResponse>) {
        when (lessonTodayList.isEmpty()) {
            true -> {
                val nothingHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOTHING)
                val nothingLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOTHING) { _, _, _ ->
                        val action =
                            TodayLessonFragmentDirections.actionTodayLessonToRegisterLessonFirst()
                        findNavController().safeNavigate(action)
                    }
                val concatAdapter = ConcatAdapter(
                    nothingHeader,
                    nothingLessonAdapter
                )
                nothingLessonAdapter.submitList(listOf(LessonTodayResponse.EMPTY))

                binding.apply {
                    rvTodayLesson.adapter = concatAdapter
                    tvTodayTitleMessage.text = getString(R.string.home_today_title_not_register)
                    ivTodaySloth.setImageResource(R.drawable.ic_home_today_sloth_not_start)
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
                                // lessonListViewModel.updateLessonCount()
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
                                showCompleteDialog(lessonToday.lessonId.toString())
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

    //TODO Flow 로 변경
    //TODO 뷰에서 명령을 내리면 안됨
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
                    is Result.UnLoading -> hideProgress()
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
                        when (result.statusCode) {
                            401 -> showForbiddenDialog(requireContext(), this@TodayLessonFragment) {
                                todayLessonViewModel.removeAuthToken()
                            }

                            else -> {
                                showToast(
                                    requireContext(),
                                    getString(R.string.lesson_info_update_fail)
                                )
                                Timber.tag("Update Error").d(result.throwable)
                            }
                        }
                    }
                }
            }
        }
    }

//    private suspend fun updateLessonTodayList(
//        lesson: LessonTodayResponse,
//        bodyType: TodayLessonAdapter.BodyType,
//        clickType: TodayLessonAdapter.ClickType,
//        delayTime: Long,
//        result: Result.Success<LessonUpdateCountResponse>,
//    ) {
//        delay(delayTime)
//        when (bodyType) {
//            TodayLessonAdapter.BodyType.NOT_FINISHED -> {
//                if (result.data.presentNumber == lesson.untilTodayNumber
//                //it.data.presentNumber == 0 || (clickType == TodayLessonAdapter.ClickType.CLICK_PLUS && it.data.presentNumber == 1)
//                ) lessonListViewModel.fetchTodayLessonList()
//            }
//            TodayLessonAdapter.BodyType.FINISHED -> {
//                if (result.data.presentNumber < lesson.untilTodayNumber ||
//                    result.data.presentNumber == lesson.totalNumber ||
//                    result.data.presentNumber + 1 == lesson.totalNumber && (clickType == TodayLessonAdapter.ClickType.CLICK_MINUS)
//                )
//                lessonListViewModel.fetchTodayLessonList()
//            }
//            else -> Unit
//        }
//    }

    private fun showCompleteDialog(lessonId: String) {
        val completeDialog = SlothDialog(requireContext(), DialogState.COMPLETE)
        completeDialog.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                todayLessonViewModel.finishLesson(lessonId)
            }
        }
        completeDialog.show()
    }
}