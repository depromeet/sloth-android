package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.ItemLessonListEmptyBinding

class LessonListEmptyViewHolder(
    private val binding: ItemLessonListEmptyBinding,
    val onClick: (LessonAllResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lesson: LessonAllResponse) {
        itemView.apply {
            binding.apply {
                btnLessonListRegister.setOnClickListener { onClick(lesson) }
            }
        }
    }
}