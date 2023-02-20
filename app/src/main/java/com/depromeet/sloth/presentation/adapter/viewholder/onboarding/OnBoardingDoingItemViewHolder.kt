package com.depromeet.sloth.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.databinding.ItemOnBoardingDoingBinding

class OnBoardingDoingItemViewHolder(val binding: ItemOnBoardingDoingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(todayLesson: TodayLessonResponse) {
        itemView.apply {
            binding.apply {
                tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
                tvTodayLessonTotalNumber.text = todayLesson.untilTodayNumber.toString()

                pbTodayLessonBar.let {
                    it.max = todayLesson.untilTodayNumber * 1000
                    it.progress = todayLesson.presentNumber * 1000
                }

                btnTodayLessonPlus.setOnClickListener {
                    viewTodayLessonLottie.playAnimation()
                }
            }
        }
    }
}