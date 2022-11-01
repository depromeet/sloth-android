package com.depromeet.sloth.ui.list.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemHomeTodayLessonNothingBinding
import com.depromeet.sloth.ui.list.adapter.TodayLessonAdapter

class TodayLessonNothingViewHolder(
    private val binding: ItemHomeTodayLessonNothingBinding,
    val onClick: (TodayLessonAdapter.ClickType, LessonTodayResponse) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(lessonToday: LessonTodayResponse) {
        itemView.apply {
            binding.apply {
                clTodayLesson.setOnClickListener {
                    onClick(TodayLessonAdapter.ClickType.CLICK_NORMAL, lessonToday)
                }
            }
        }
    }
}