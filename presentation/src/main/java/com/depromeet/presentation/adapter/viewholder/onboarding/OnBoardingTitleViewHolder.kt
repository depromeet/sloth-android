package com.depromeet.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.ItemOnBoardingTitleBinding
import com.depromeet.presentation.ui.onboarding.OnBoardingType


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