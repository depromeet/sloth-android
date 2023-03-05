package com.depromeet.sloth.presentation.adapter.viewholder.lessonlist

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ItemLessonListTitleBinding
import com.depromeet.sloth.presentation.screen.lessonlist.LessonListType

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
            }
            tvLessonListCount.text = itemView.context.getString(R.string.lesson_count_unit, count.toString())
        }
    }
}