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
                remainNumber = 1,
                presentNumber = 4,
                remainDay = 9,
                weeklyFinished = false
            ),
            WeeklyLessonResponse(
                categoryName = "디자인",
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                remainNumber = 0,
                presentNumber = 5,
                remainDay = 19,
                weeklyFinished = false
            ),
            WeeklyLessonResponse(
                categoryName = "기획",
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                remainNumber = 1,
                presentNumber = 4,
                remainDay = 10,
                weeklyFinished = false
            ),
            WeeklyLessonResponse(
                categoryName = "개발",
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                remainNumber = 3,
                presentNumber = 3,
                remainDay = 7,
                weeklyFinished = true
            ),
            WeeklyLessonResponse(
                categoryName = "디자인",
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                remainNumber = 6,
                presentNumber = 6,
                remainDay = 11,
                weeklyFinished = true
            ),
            WeeklyLessonResponse(
                categoryName = "기획",
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                remainNumber = 8,
                presentNumber = 8,
                remainDay = 1,
                weeklyFinished = true
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
                dummyList.filter { it.weeklyFinished }
            )
            notFinishedLessonAdapter.submitList(
                dummyList.filter { it.weeklyFinished.not() }
            )
        }

        binding.rvTodayLesson.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }
}