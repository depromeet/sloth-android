package com.depromeet.presentation.adapter.viewholder.todaylesson

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.ItemTodayLessonHeaderBinding
import com.depromeet.presentation.ui.todaylesson.TodayLessonType
import com.depromeet.presentation.util.GlideApp


class TodayLessonHeaderViewHolder(val binding: ItemTodayLessonHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(headerType: TodayLessonType) {
        binding.apply {
            when(headerType) {
                TodayLessonType.EMPTY -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_empty)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_empty).into(ivTodayLessonHeader)
                }
                TodayLessonType.NOT_START -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_not_start)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_not_start).into(ivTodayLessonHeader)
                }
                TodayLessonType.DOING -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_doing)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_doing).into(ivTodayLessonHeader)
                }
                TodayLessonType.FINISHED -> {
                    tvTodayLessonHeader.setText(R.string.today_lesson_header_finished)
                    GlideApp.with(itemView.context).load(R.drawable.ic_today_lesson_header_finished).into(ivTodayLessonHeader)
                }
            }
        }
    }
}