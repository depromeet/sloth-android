package com.depromeet.sloth.ui.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.FragmentLessonListBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.adapter.HeaderAdapter
import com.depromeet.sloth.ui.adapter.LessonListAdapter
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.LessonItemDecoration
import com.depromeet.sloth.util.DATE_FORMAT_PATTERN
import com.depromeet.sloth.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class LessonListFragment : BaseFragment<FragmentLessonListBinding>(R.layout.fragment_lesson_list) {

    private val lessonListViewModel: LessonListViewModel by viewModels()

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
                fetchLessonListSuccess.
                    collect{
                        closeNetworkError()
                        setLessonList(it)
                    }
            }

            launch {
                fetchLessonFail
                    .collect { statusCode ->
                        when (statusCode) {
                            401 -> showForbiddenDialog(
                                requireContext(),
                                this@LessonListFragment
                            ) { removeAuthToken() }

                            else -> { showNetworkError() }
                        }
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
                        showWaitDialog(requireContext())
                    }
            }
        }
    }

    private fun showNetworkError() {
        binding.lessonListNetworkError.itemNetworkError.visibility = View.VISIBLE
    }

    private fun closeNetworkError() {
        binding.lessonListNetworkError.itemNetworkError.visibility = View.GONE
    }

    private fun navigateToLessonDetail(lessonInfo: LessonAllResponse) {
        val action = LessonListFragmentDirections.actionLessonListToLessonDetail(
            lessonInfo.lessonId.toString()
        )
        findNavController().safeNavigate(action)
    }

    private fun setLessonList(lessonInfo: List<LessonAllResponse>) {
        when (lessonInfo.isEmpty()) {
            true -> {
                binding.tbLessonList.menu.findItem(R.id.menu_register_lesson).isVisible = false
                val nothingLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.NOTHING) {
                        val action =
                            LessonListFragmentDirections.actionLessonListToRegisterLessonFirst()
                        findNavController().safeNavigate(action)
                    }

                nothingLessonAdapter.submitList(listOf(LessonAllResponse.EMPTY))
                binding.rvLessonList.adapter = nothingLessonAdapter
            }

            false -> {
                binding.tbLessonList.menu.findItem(R.id.menu_register_lesson).isVisible = true
                val lessonDoingList = mutableListOf<LessonAllResponse>()
                val lessonPlanningList = mutableListOf<LessonAllResponse>()
                val lessonPassedList = mutableListOf<LessonAllResponse>()

                lessonInfo.forEach { lesson ->
                    when (getLessonType(lesson)) {
                        LessonListAdapter.BodyType.PASSED -> lessonPassedList.add(lesson)
                        LessonListAdapter.BodyType.PLANNING -> lessonPlanningList.add(lesson)
                        else -> lessonDoingList.add(lesson)
                    }
                }

                val doingHeader = HeaderAdapter(HeaderAdapter.HeaderType.DOING, lessonDoingList.size)
                val planningHeader = HeaderAdapter(HeaderAdapter.HeaderType.PLANNING, lessonPlanningList.size)
                val passedHeader = HeaderAdapter(HeaderAdapter.HeaderType.PASSED, lessonPassedList.size)
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
    private fun getLessonType(
        lessonInfo: LessonAllResponse,
    ): LessonListAdapter.BodyType {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_PATTERN)
        val startDate = dateFormat.parse(lessonInfo.startDate)
        val todayDate = Calendar.getInstance()
        val isPassed = lessonInfo.isFinished || lessonInfo.lessonStatus == PAST
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