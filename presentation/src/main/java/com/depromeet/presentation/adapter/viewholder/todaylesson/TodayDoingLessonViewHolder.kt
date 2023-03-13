package com.depromeet.presentation.adapter.viewholder.todaylesson

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.databinding.ItemTodayLessonDoingBinding
import com.depromeet.presentation.model.TodayLesson


class TodayDoingLessonViewHolder(
    val binding: ItemTodayLessonDoingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(todayLesson: TodayLesson) {
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