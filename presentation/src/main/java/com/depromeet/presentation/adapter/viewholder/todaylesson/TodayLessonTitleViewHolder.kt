package com.depromeet.presentation.adapter.viewholder.todaylesson

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.ItemTodayLessonTitleBinding
import com.depromeet.presentation.ui.todaylesson.TodayLessonType


class TodayLessonTitleViewHolder(val binding: ItemTodayLessonTitleBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(headerType: TodayLessonType) {
        binding.apply {
            when(headerType) {
                TodayLessonType.EMPTY -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_doing_lesson)
                }
                TodayLessonType.DOING -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_doing_lesson)
                }
                TodayLessonType.FINISHED -> {
                    tvTodayLessonTitle.setText(R.string.today_lesson_title_finished_lesson)
                }
                else -> throw IllegalStateException("Unknown headerType $headerType")
            }
        }
    }
}