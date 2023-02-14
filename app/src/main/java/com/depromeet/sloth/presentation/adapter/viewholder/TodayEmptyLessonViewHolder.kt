package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemTodayLessonEmptyBinding

class TodayEmptyLessonViewHolder(
    private val binding: ItemTodayLessonEmptyBinding,
    val onClick: () -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(lessonToday: LessonTodayResponse) {
        itemView.apply {
            binding.apply {
                clTodayLesson.setOnClickListener {
                    onClick
                }
            }
        }
    }

//    companion object {
//        const val DELAY_TIME = 200L
//    }
}