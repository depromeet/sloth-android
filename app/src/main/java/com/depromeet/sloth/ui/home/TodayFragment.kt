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

        val finishedHeader = HeaderAdapter(HeaderAdapter.HeaderType.NOT_FINISHED)
        val finishedLessonAdapter = TodayLessonAdapter()
        val concatAdapter = ConcatAdapter(finishedHeader, finishedLessonAdapter)
        finishedLessonAdapter.submitList(
            listOf(
                TodayLessonResponse(remainDay = 9),
                TodayLessonResponse(remainDay = 19),
                TodayLessonResponse(remainDay = 10)
            )
        )

        binding.rvTodayLesson.let {
            it.addItemDecoration(LessonItemDecoration(requireContext(), 16))
            it.adapter = concatAdapter
        }
    }
}