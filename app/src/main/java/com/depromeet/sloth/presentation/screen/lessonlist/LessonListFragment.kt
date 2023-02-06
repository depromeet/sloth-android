package com.depromeet.sloth.presentation.screen.lessonlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.FragmentLessonListBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.presentation.adapter.HeaderAdapter
import com.depromeet.sloth.presentation.adapter.LessonListAdapter
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.custom.LessonItemDecoration
import com.depromeet.sloth.util.DATE_FORMAT_PATTERN
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

//TODO 어댑터에 생성자에 있는 Enum class 제거
@AndroidEntryPoint
class LessonListFragment : BaseFragment<FragmentLessonListBinding>(R.layout.fragment_lesson_list) {

    private val lessonListViewModel: LessonListViewModel by viewModels()
    // private lateinit var lessonListAdapter: LessonListAdapter

    override fun onStart() {
        super.onStart()
        lessonListViewModel.fetchAllLessonList()
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

    override fun initViews() = with(binding) {
        rvLessonList.addItemDecoration(LessonItemDecoration(requireActivity(), 16))
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
                checkOnBoardingCompleteEvent
                    .collect { isCompleteOnBoarding ->
                        when (isCompleteOnBoarding) {
                            true -> Unit
                            // TODO UDF 위반
                            false -> showOnBoardingCheckDetail()
                        }
                    }
            }

            launch {
                showOnBoardingCheckDetailEvent
                    .collect {
                        val action =
                            LessonListFragmentDirections.actionLessonListToOnBoardingCheckDetailDialog()
                        findNavController().safeNavigate(action)
                    }
            }

            // TODO list 자체를 구독하는 형식으로 변경
            // clickEvent 를 통해 뷰모델 내의 navigation event 함수를 전달 하는 형식으로
            launch {
                fetchLessonListSuccessEvent
                    .collect {
                        setLessonList(it)
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

    private fun navigateToLessonDetail(lesson: LessonAllResponse) {
        //TODO UDF 위반, 단일 책임 원칙 위반
        lessonListViewModel.updateOnBoardingStatus()
        val action = LessonListFragmentDirections.actionLessonListToLessonDetail(
            lesson.lessonId.toString()
        )
        findNavController().safeNavigate(action)
    }

    private fun setLessonList(lessonList: List<LessonAllResponse>) {
        when (lessonList.isEmpty()) {
            true -> {
                binding.tbLessonList.menu.findItem(R.id.menu_register_lesson).isVisible = false
                val emptyLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.Empty) {
                        val action =
                            LessonListFragmentDirections.actionLessonListToRegisterLessonFirst()
                        findNavController().safeNavigate(action)
                    }

                emptyLessonAdapter.submitList(listOf(LessonAllResponse.EMPTY))
                binding.rvLessonList.adapter = emptyLessonAdapter
            }

            false -> {
                binding.tbLessonList.menu.findItem(R.id.menu_register_lesson).isVisible = true
                val lessonDoingList = mutableListOf<LessonAllResponse>()
                val lessonPlanningList = mutableListOf<LessonAllResponse>()
                val lessonPassedList = mutableListOf<LessonAllResponse>()

                lessonList.forEach { lesson ->
                    when (getLessonType(lesson)) {
                        LessonListAdapter.BodyType.PASSED -> lessonPassedList.add(lesson)
                        LessonListAdapter.BodyType.PLANNING -> lessonPlanningList.add(lesson)
                        else -> lessonDoingList.add(lesson)
                    }
                }

                val doingHeader =
                    HeaderAdapter(HeaderAdapter.HeaderType.DOING, lessonDoingList.size)
                val planningHeader =
                    HeaderAdapter(HeaderAdapter.HeaderType.PLANNING, lessonPlanningList.size)
                val passedHeader =
                    HeaderAdapter(HeaderAdapter.HeaderType.PASSED, lessonPassedList.size)
                val doingLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.DOING) { lesson ->
                        navigateToLessonDetail(lesson)
                    }
                val planningLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.PLANNING) { lesson ->
                        navigateToLessonDetail(lesson)
                    }
                val passedLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.PASSED) { lesson ->
                        navigateToLessonDetail(lesson)
                    }
                val concatAdapter = ConcatAdapter(
                    doingHeader,
                    doingLessonAdapter,
                    planningHeader,
                    planningLessonAdapter,
                    passedHeader,
                    passedLessonAdapter
                )

                if (lessonDoingList.isEmpty()) {
                    concatAdapter.removeAdapter(doingHeader)
                    concatAdapter.removeAdapter(doingLessonAdapter)
                } else {
                    doingLessonAdapter.submitList(lessonDoingList)
                }

                if (lessonPlanningList.isEmpty()) {
                    concatAdapter.removeAdapter(planningHeader)
                    concatAdapter.removeAdapter(planningLessonAdapter)
                } else {
                    planningLessonAdapter.submitList(lessonPlanningList)
                }

                if (lessonPassedList.isEmpty()) {
                    concatAdapter.removeAdapter(passedHeader)
                    concatAdapter.removeAdapter(passedLessonAdapter)
                } else {
                    passedLessonAdapter.submitList(lessonPassedList)
                }

                binding.rvLessonList.adapter = concatAdapter
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getLessonType(lesson: LessonAllResponse): LessonListAdapter.BodyType {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_PATTERN)
        val startDate = dateFormat.parse(lesson.startDate)
        val todayDate = Calendar.getInstance()
        val isPassed = lesson.isFinished || lesson.lessonStatus == PAST
        val isPlanning = (todayDate.time.time - startDate!!.time) < 0L

        return when {
            isPassed -> LessonListAdapter.BodyType.PASSED
            isPlanning -> LessonListAdapter.BodyType.PLANNING
            else -> LessonListAdapter.BodyType.DOING
        }
    }

    companion object {
        private const val PAST = "PAST"
    }
}