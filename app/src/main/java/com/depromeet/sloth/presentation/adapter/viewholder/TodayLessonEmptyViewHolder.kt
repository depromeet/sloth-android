package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemTodayLessonEmptyBinding
import com.depromeet.sloth.presentation.adapter.TodayLessonAdapter

class TodayLessonEmptyViewHolder(
    private val binding: ItemTodayLessonEmptyBinding,
    val onClick: (TodayLessonAdapter.ClickType, LessonTodayResponse, Long) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(lessonToday: LessonTodayResponse) {
        itemView.apply {
            binding.apply {
                clTodayLesson.setOnClickListener {
                    onClick(TodayLessonAdapter.ClickType.CLICK_NORMAL, lessonToday, DELAY_TIME)
                }
            }
        }
    }

    companion object {
        const val DELAY_TIME = 200L
    }
}