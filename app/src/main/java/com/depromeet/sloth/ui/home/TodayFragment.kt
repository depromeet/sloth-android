package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.home.LessonState
import com.depromeet.sloth.data.network.home.AllLessonResponse
import com.depromeet.sloth.data.network.home.WeeklyLessonResponse
import com.depromeet.sloth.databinding.FragmentTodayBinding
import com.depromeet.sloth.ui.base.BaseFragment

class TodayFragment : BaseFragment<LessonViewModel, FragmentTodayBinding>() {
    private val preferenceManager: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: LessonViewModel
        get() = LessonViewModel()

    override fun getViewBinding(): FragmentTodayBinding =
        FragmentTodayBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var lessonList = listOf<AllLessonResponse>()

        mainScope {
            preferenceManager.getAccessToken()?.run {
                viewModel.fetchLessonAllList(
                    accessToken = this
                ).let {
                    when (it) {
                        is LessonState.Success<List<AllLessonResponse>> -> {
                            lessonList = it.data
                            //등록된 리스트를 불러온 후에 어댑터와 연결하는 작업 필요함
                        }
                        is LessonState.Error -> {
                            Log.d("Error", "${it.exception}")
                        }
                        is LessonState.Unauthorized -> {
                            //정책 확인 필요
                            Log.d("Error", "Unauthorized")
                        }
                        is LessonState.NotFound -> {
                            Log.d("Error", "NotFound")
                        }
                        is LessonState.Forbidden -> {
                            Log.d("Error", "Forbidden")
                        }
                    }
                }
            }
        }

        setTestData()
    }

    private fun setTestData() {
        val dummyList = listOf<WeeklyLessonResponse>(
            WeeklyLessonResponse(
                categoryName = "개발",
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                presentNumber = 4,
                remainDay = 9,
                untilTodayFinished = false,
                untilTodayNumber = 8
            ),
            WeeklyLessonResponse(
                categoryName = "디자인",
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                presentNumber = 5,
                remainDay = 19,
                untilTodayFinished = true,
                untilTodayNumber = 5
            ),
            WeeklyLessonResponse(
                categoryName = "기획",
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                presentNumber = 4,
                remainDay = 10,
                untilTodayFinished = false,
                untilTodayNumber = 6
            ),
            WeeklyLessonResponse(
                categoryName = "개발",
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                presentNumber = 6,
                remainDay = 7,
                untilTodayFinished = true,
                untilTodayNumber = 6
            ),
            WeeklyLessonResponse(
                categoryName = "디자인",
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                presentNumber = 1,
                remainDay = 11,
                untilTodayFinished = false,
                untilTodayNumber = 4
            ),
            WeeklyLessonResponse(
                categoryName = "기획",
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                presentNumber = 2,
                remainDay = 1,
                untilTodayFinished = false,
                untilTodayNumber = 3
            )
        )

        val notFinishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
        val finishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)
        val notFinishedLessonAdapter = TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED)
        val finishedLessonAdapter = TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED)
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