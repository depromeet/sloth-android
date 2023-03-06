package com.depromeet.sloth.presentation.adapter.viewholder.todaylesson

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.databinding.ItemTodayLessonDoingBinding

class TodayDoingLessonViewHolder(
    val binding: ItemTodayLessonDoingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(todayLesson: TodayLessonResponse) {
        itemView.apply {
            binding.apply {
                tvTodayLessonRemain.text =
                    if (todayLesson.remainDay == 0) "D-Day" else "D-${todayLesson.remainDay}"
                tvTodayLessonCategory.text = todayLesson.categoryName
                tvTodayLessonSite.text = todayLesson.siteName
                tvTodayLessonName.text = todayLesson.lessonName
                tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
                tvTodayLessonTotalNumber.text = todayLesson.untilTodayNumber.toString()

                pbTodayLessonBar.let {
                    it.max = todayLesson.untilTodayNumber * 1000
                    it.progress = todayLesson.presentNumber * 1000
                }
            }
        }
    }
}