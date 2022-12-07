package com.depromeet.sloth.ui.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.R
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.FragmentListBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showWaitDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.LessonItemDecoration
import com.depromeet.sloth.ui.detail.LessonDetailActivity
import com.depromeet.sloth.ui.list.adapter.HeaderAdapter
import com.depromeet.sloth.ui.list.adapter.LessonListAdapter
import com.depromeet.sloth.ui.register.RegisterLessonActivity
import com.depromeet.sloth.util.DATE_FORMAT_PATTERN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ListFragment : BaseFragment<FragmentListBinding>(R.layout.fragment_list) {

    private val lessonListViewModel: LessonListViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = lessonListViewModel
        }

        initViews()
        initObserver()
    }

    override fun initViews() = with(binding) {
        rvLessonList.addItemDecoration(LessonItemDecoration(requireActivity(), 16))
    }

    private fun initObserver() = with(lessonListViewModel) {
        repeatOnStarted {
            launch {
                allLessonList
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<List<LessonAllResponse>> -> setLessonList(result.data)
                            is Result.Error -> {
                                when(result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                            lessonListViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(getString(R.string.lesson_info_fetch_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                onRegisterLessonClick
                    .collect {
                        moveRegisterActivity()
                    }
            }

            launch {
                onNavigateToNotificationListClick
                    .collect {
                        showWaitDialog(requireContext())
                    }
            }
        }
    }

    private fun moveRegisterActivity() {
        startActivity(Intent(requireActivity(), RegisterLessonActivity::class.java))
    }

    private fun moveDetailActivity(lessonInfo: LessonAllResponse) {
        startActivity(
            Intent(requireContext(), LessonDetailActivity::class.java).apply {
                putExtra(LESSON_ID, lessonInfo.lessonId.toString())
            }
        )
    }

    private fun setLessonList(lessonInfo: List<LessonAllResponse>) {
        when (lessonInfo.isEmpty()) {
            true -> {
                binding.ivLessonListRegister.visibility = View.INVISIBLE
                val nothingLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.NOTHING) { _ -> moveRegisterActivity() }

                nothingLessonAdapter.submitList(listOf(LessonAllResponse.EMPTY))
                binding.rvLessonList.adapter = nothingLessonAdapter
            }

            false -> {
                binding.ivLessonListRegister.visibility = View.VISIBLE

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

                val doingHeader = HeaderAdapter(HeaderAdapter.HeaderType.DOING)
                val planningHeader = HeaderAdapter(HeaderAdapter.HeaderType.PLANNING)
                val passedHeader = HeaderAdapter(HeaderAdapter.HeaderType.PASSED)
                val doingLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.DOING) { lesson ->
                        moveDetailActivity(lesson)
                    }
                val planningLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.PLANNING) { lesson ->
                        moveDetailActivity(lesson)
                    }
                val passedLessonAdapter =
                    LessonListAdapter(LessonListAdapter.BodyType.PASSED) { lesson ->
                        moveDetailActivity(lesson)
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
        private const val LESSON_ID = "lessonId"
        private const val PAST = "PAST"
    }
}