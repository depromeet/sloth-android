package com.depromeet.sloth.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ItemOnBoardingTitleBinding
import com.depromeet.sloth.presentation.screen.onboarding.OnBoardingType

class OnBoardingTitleViewHolder(val binding: ItemOnBoardingTitleBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(headerType: OnBoardingType) {
        binding.apply {
            when(headerType) {
                OnBoardingType.EMPTY -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_empty_lesson)
                }

                OnBoardingType.DOING -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_doing_lesson)
                }
                OnBoardingType.FINISHED -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_finished_lesson)
                }
            }
        }
    }
}