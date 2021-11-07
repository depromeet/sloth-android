package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.home.LessonListState
import com.depromeet.sloth.data.network.home.LessonResponse
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

        var lessonList = listOf<LessonResponse>()

        mainScope {
            preferenceManager.getAccessToken()?.run {
                viewModel.fetchLessonList(
                    accessToken = this
                ).let {
                    when (it) {
                        is LessonListState.Success<List<LessonResponse>> -> {
                            lessonList = it.data
                            //등록된 리스트를 불러온 후에 어댑터와 연결하는 작업 필요함
                        }
                        is LessonListState.Error -> {
                            Log.d("Error", "${it.exception}")
                        }
                        is LessonListState.Unauthorized -> {
                            //정책 확인 필요
                            Log.d("Error", "Unauthorized")
                        }
                        is LessonListState.NotFound -> {
                            Log.d("Error", "NotFound")
                        }
                        is LessonListState.Forbidden -> {
                            Log.d("Error", "Forbidden")
                        }
                    }
                }
            }
        }

        setTestData()
    }

    private fun setTestData() {
        val dummyList = listOf<LessonResponse>(
            LessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                categoryName = "개발",
                currentProgressRate = 1,
                goalProgressRate = 4,
                remainDay = 9,
                isFinished = false
            ),
            LessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                categoryName = "디자인",
                currentProgressRate = 0,
                goalProgressRate = 5,
                remainDay = 19,
                isFinished = false
            ),
            LessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 1,
                goalProgressRate = 4,
                remainDay = 10,
                isFinished = false
            ),
            LessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                categoryName = "개발",
                currentProgressRate = 3,
                goalProgressRate = 3,
                remainDay = 7,
                isFinished = true
            ),
            LessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                categoryName = "디자인",
                currentProgressRate = 6,
                goalProgressRate = 6,
                remainDay = 11,
                isFinished = true
            ),
            LessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 8,
                goalProgressRate = 8,
                remainDay = 1,
                isFinished = true
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
                dummyList.filter { it.isFinished }
            )
            notFinishedLessonAdapter.submitList(
                dummyList.filter { it.isFinished.not() }
            )
        }

        binding.rvTodayLesson.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }
}