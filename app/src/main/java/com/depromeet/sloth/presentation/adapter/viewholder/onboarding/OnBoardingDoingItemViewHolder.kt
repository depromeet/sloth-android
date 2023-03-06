package com.depromeet.sloth.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.databinding.ItemOnBoardingDoingBinding

class OnBoardingDoingItemViewHolder(val binding: ItemOnBoardingDoingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(onBoardingItem: TodayLessonResponse) {
        itemView.apply {
            binding.apply {
                tvTodayLessonCurrentNumber.text = onBoardingItem.presentNumber.toString()
                tvTodayLessonTotalNumber.text = onBoardingItem.untilTodayNumber.toString()
                pbTodayLessonBar.let {
                    it.max = onBoardingItem.untilTodayNumber * 1000
                    it.progress = onBoardingItem.presentNumber * 1000
                }
            }
        }
    }
}