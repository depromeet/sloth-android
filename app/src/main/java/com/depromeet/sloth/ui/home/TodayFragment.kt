package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import com.depromeet.sloth.data.network.home.TodayLessonResponse
import com.depromeet.sloth.databinding.FragmentTodayBinding

class TodayFragment : Fragment() {

    private lateinit var binding: FragmentTodayBinding
    private var _binding: FragmentTodayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodayBinding.inflate(layoutInflater, container, false)
        binding = _binding!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyList = listOf<TodayLessonResponse>(
            TodayLessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                categoryName = "개발",
                currentProgressRate = 1,
                goalProgressRate = 4,
                remainDay = 9,
                isFinished = false
            ),
            TodayLessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                categoryName = "디자인",
                currentProgressRate = 0,
                goalProgressRate = 5,
                remainDay = 19,
                isFinished = false
            ),
            TodayLessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 고급 (Inflearn Original)",
                categoryName = "기획",
                currentProgressRate = 1,
                goalProgressRate = 4,
                remainDay = 10,
                isFinished = false
            ),
            TodayLessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 초급 (Inflearn Original)",
                categoryName = "개발",
                currentProgressRate = 3,
                goalProgressRate = 3,
                remainDay = 7,
                isFinished = true
            ),
            TodayLessonResponse(
                lessonName = "프로그래밍 시작하기 : \n파이썬 중급 (Inflearn Original)",
                categoryName = "디자인",
                currentProgressRate = 6,
                goalProgressRate = 6,
                remainDay = 11,
                isFinished = true
            ),
            TodayLessonResponse(
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