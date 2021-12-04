package com.depromeet.sloth.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.home.LessonState
import com.depromeet.sloth.data.network.home.TodayLessonResponse
import com.depromeet.sloth.databinding.FragmentTodayBinding
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.detail.LessonDetailActivity
import com.depromeet.sloth.ui.register.RegisterLessonFirstActivity

class TodayFragment : BaseFragment<LessonViewModel, FragmentTodayBinding>() {
    private val preferenceManager: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: LessonViewModel
        get() = LessonViewModel()

    override fun getViewBinding(): FragmentTodayBinding =
        FragmentTodayBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainScope {
            preferenceManager.getAccessToken()?.run {
                viewModel.fetchTodayLessonList(
                    accessToken = this
                ).let {
                    when (it) {
                        is LessonState.Success<List<TodayLessonResponse>> -> {
                            updateLessonList(it.data)
                        }
                        is LessonState.Error -> {
                            Log.d("Error", "${it.exception}")
                        }
                        is LessonState.Unauthorized -> {
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

        //setTestData()
    }

    private fun moveRegisterActivity() {
        val intent = Intent(requireContext(), RegisterLessonFirstActivity::class.java)
        startActivity(intent)
    }

    private fun moveDetailActivity(lesson: TodayLessonResponse) {
        val intent = Intent(requireContext(), LessonDetailActivity::class.java)
        intent.putExtra("lessonId", lesson.lessonId.toString())
        startActivity(intent)
    }

    private fun updateLessonList(lessonList: List<TodayLessonResponse>) {
        when (lessonList.isEmpty()) {
            true -> {
                val nothingHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOTHING)
                val nothingLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOTHING) { _ -> moveRegisterActivity() }
                val concatAdapter = ConcatAdapter(
                    nothingHeader,
                    nothingLessonAdapter
                )

                nothingLessonAdapter.submitList(listOf(TodayLessonResponse.EMPTY))
                binding.rvTodayLesson.adapter = concatAdapter
            }

            false -> {
                val notFinishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
                val finishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)
                val notFinishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED) { lesson ->
                        moveDetailActivity(lesson)
                    }
                val finishedLessonAdapter =
                    TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED) { lesson ->
                        moveDetailActivity(lesson)
                    }
                val concatAdapter = ConcatAdapter(
                    notFinishedHeader,
                    notFinishedLessonAdapter,
                    finishedHeader,
                    finishedLessonAdapter
                )

                lessonList.let {
                    finishedLessonAdapter.submitList(
                        lessonList.filter { it.untilTodayFinished }
                    )
                    notFinishedLessonAdapter.submitList(
                        lessonList.filter { it.untilTodayFinished.not() }
                    )
                }

                binding.rvTodayLesson.let {
                    it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
                    it.adapter = concatAdapter
                }
            }
        }
    }

    private fun setTestData() {
        val dummyList = listOf<TodayLessonResponse>(
            TodayLessonResponse(
                categoryName = "개발",
                lessonId = 1,
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                presentNumber = 4,
                remainDay = 9,
                siteName = "TEST1",
                untilTodayFinished = false,
                untilTodayNumber = 8
            ),
            TodayLessonResponse(
                categoryName = "디자인",
                lessonId = 2,
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                presentNumber = 5,
                remainDay = 19,
                siteName = "TEST2",
                untilTodayFinished = true,
                untilTodayNumber = 5
            ),
            TodayLessonResponse(
                categoryName = "기획",
                lessonId = 3,
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                presentNumber = 4,
                remainDay = 10,
                siteName = "TEST3",
                untilTodayFinished = false,
                untilTodayNumber = 6
            ),
            TodayLessonResponse(
                categoryName = "개발",
                lessonId = 4,
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                presentNumber = 6,
                remainDay = 7,
                siteName = "TEST4",
                untilTodayFinished = true,
                untilTodayNumber = 6
            ),
            TodayLessonResponse(
                categoryName = "디자인",
                lessonId = 5,
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                presentNumber = 1,
                remainDay = 11,
                siteName = "TEST5",
                untilTodayFinished = false,
                untilTodayNumber = 4
            ),
            TodayLessonResponse(
                categoryName = "기획",
                lessonId = 6,
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                presentNumber = 2,
                remainDay = 1,
                siteName = "TEST6",
                untilTodayFinished = false,
                untilTodayNumber = 3
            )
        )

        val notFinishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
        val finishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.FINISHED)
        val notFinishedLessonAdapter =
            TodayLessonAdapter(TodayLessonAdapter.BodyType.NOT_FINISHED) { lesson ->
                moveDetailActivity(lesson)
            }
        val finishedLessonAdapter =
            TodayLessonAdapter(TodayLessonAdapter.BodyType.FINISHED) { lesson ->
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