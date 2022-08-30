package com.depromeet.sloth.ui.list.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.databinding.ItemHomeLessonListNothingBinding

class LessonListNothingViewHolder(
    private val binding: ItemHomeLessonListNothingBinding,
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