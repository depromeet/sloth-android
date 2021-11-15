package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.home.LessonResponse
import com.depromeet.sloth.databinding.FragmentClassBinding
import com.depromeet.sloth.ui.base.BaseFragment

class ClassFragment: BaseFragment<LessonViewModel, FragmentClassBinding>() {
    private val preferenceManager: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: LessonViewModel
        get() = LessonViewModel()

    override fun getViewBinding(): FragmentClassBinding =
        FragmentClassBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var lessonList = listOf<LessonResponse>()

        mainScope {

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

        val doingHeader = HeaderAdapter(HeaderAdapter.HeaderType.DOING)
        val planningHeader = HeaderAdapter(HeaderAdapter.HeaderType.PLANNING)
        val passedHeader = HeaderAdapter(HeaderAdapter.HeaderType.PASSED)

        val doingLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.DOING)
        val planningLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.PLANNING)
        val passedLessonAdapter = ClassLessonAdapter(ClassLessonAdapter.BodyType.PASSED)

        val concatAdapter = ConcatAdapter(
            doingHeader,
            doingLessonAdapter
            //planningHeader,
            //planningLessonAdapter,
            //passedHeader,
            //passedLessonAdapter
        )

        dummyList.let {
            doingLessonAdapter.submitList(
                dummyList.filter { it.isFinished }
            )
            planningLessonAdapter.submitList(
                dummyList
            )
            passedLessonAdapter.submitList(
                dummyList.filter { it.isFinished.not() }
            )
        }

        binding.rvClassLesson.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }
}