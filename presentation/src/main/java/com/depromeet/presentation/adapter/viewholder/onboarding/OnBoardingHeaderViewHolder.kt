package com.depromeet.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.ItemOnBoardingHeaderBinding
import com.depromeet.presentation.ui.onboarding.OnBoardingType
import com.depromeet.presentation.util.GlideApp


class OnBoardingHeaderViewHolder(val binding: ItemOnBoardingHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(headerType: OnBoardingType) {
        binding.apply {
            when(headerType) {
                OnBoardingType.EMPTY -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_on_boarding_empty)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_empty).into(ivTodayLessonHeader)
                }

                OnBoardingType.DOING -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_on_boarding_start)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_doing).into(ivTodayLessonHeader)
                }
                OnBoardingType.FINISHED -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_on_boarding_finished)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_on_boarding_finished).into(ivTodayLessonHeader)
                }
            }
        }
    }
}