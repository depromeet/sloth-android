package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.ItemLessonListEmptyBinding

class EmptyLessonListViewHolder(
    private val binding: ItemLessonListEmptyBinding,
    val onClick: (LessonListResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lesson: LessonListResponse) {
        itemView.apply {
            binding.apply {
                btnLessonListRegister.setOnClickListener { onClick(lesson) }
            }
        }
    }
}