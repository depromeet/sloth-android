package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.ItemHomeLessonListEmptyBinding

class LessonListEmptyViewHolder(
    private val binding: ItemHomeLessonListEmptyBinding,
    val onClick: (LessonAllResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lessonInfo: LessonAllResponse) {
        itemView.apply {
            binding.apply {
                btnLessonListRegister.setOnClickListener { onClick(lessonInfo) }
            }
        }
    }
}