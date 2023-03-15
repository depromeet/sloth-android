package com.depromeet.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.databinding.ItemOnBoardingDoingBinding
import com.depromeet.presentation.model.TodayLesson


class OnBoardingDoingItemViewHolder(val binding: ItemOnBoardingDoingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(onBoardingItem: TodayLesson) {
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