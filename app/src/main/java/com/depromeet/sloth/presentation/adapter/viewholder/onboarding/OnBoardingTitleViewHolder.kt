package com.depromeet.sloth.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ItemOnBoardingTitleBinding
import com.depromeet.sloth.presentation.screen.todaylesson.TodayLessonType

class OnBoardingTitleViewHolder(val binding: ItemOnBoardingTitleBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(headerType: TodayLessonType) {
        binding.apply {
            when(headerType) {
                TodayLessonType.DOING -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_doing_lesson)
                }
                TodayLessonType.FINISHED -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_finished_lesson)
                }
            }
        }
    }
}