package com.depromeet.presentation.adapter.viewholder.lessonlist

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.ItemLessonListTitleBinding
import com.depromeet.presentation.ui.lessonlist.LessonListType


class LessonListTitleViewHolder(val binding: ItemLessonListTitleBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(headerType: LessonListType, count: Int?) {
        binding.apply {
            when(headerType) {
                LessonListType.CURRENT -> {
                    tvLessonListTitle.setText(R.string.lesson_list_title_current_lesson)
                }
                LessonListType.PLAN -> {
                    tvLessonListTitle.setText(R.string.lesson_list_title_plan_lesson)
                }
                LessonListType.PAST -> {
                    tvLessonListTitle.setText(R.string.lesson_list_title_past_lesson)
                }
                else -> throw IllegalStateException("Unknown headerType $headerType")
            }
            tvLessonListCount.text = itemView.context.getString(R.string.lesson_count_unit, count.toString())
        }
    }
}