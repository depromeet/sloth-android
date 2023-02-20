package com.depromeet.sloth.presentation.adapter.viewholder.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ItemOnBoardingHeaderBinding
import com.depromeet.sloth.presentation.screen.todaylesson.TodayLessonType
import com.depromeet.sloth.util.GlideApp

class OnBoardingHeaderViewHolder(val binding: ItemOnBoardingHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(headerType: TodayLessonType) {
        binding.apply {
            when(headerType) {
                TodayLessonType.DOING -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_on_boarding_start)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_doing).into(ivTodayLessonHeader)
                }
                TodayLessonType.FINISHED -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_on_boarding_finished)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_on_boarding_finished).into(ivTodayLessonHeader)
                }
            }
        }
    }
}