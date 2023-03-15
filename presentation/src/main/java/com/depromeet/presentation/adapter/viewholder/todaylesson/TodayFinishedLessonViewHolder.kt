package com.depromeet.presentation.adapter.viewholder.todaylesson

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.ItemTodayLessonFinishedBinding
import com.depromeet.presentation.model.TodayLesson


class TodayFinishedLessonViewHolder(
    val binding: ItemTodayLessonFinishedBinding,
): RecyclerView.ViewHolder(binding.root) {
    fun bind(todayLesson: TodayLesson) {
        itemView.apply {
            binding.apply {
                tvTodayLessonRemain.text = if (todayLesson.remainDay == 0) "D-Day" else "D-${todayLesson.remainDay}"
                tvTodayLessonCategory.text = todayLesson.categoryName
                tvTodayLessonSite.text = todayLesson.siteName
                tvTodayLessonName.text = todayLesson.lessonName
                tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
                tvTodayLessonTotalNumber.text = todayLesson.untilTodayNumber.toString()

                if(todayLesson.presentNumber == todayLesson.totalNumber) {
                    clTodayFinishedTop.setBackgroundResource(R.drawable.bg_today_lesson_finished_top)
                    clTodayFinishedBottom.visibility = View.VISIBLE
                } else {
                    clTodayFinishedTop.setBackgroundResource(R.drawable.bg_today_lesson_not_finished_top)
                    clTodayFinishedBottom.visibility = View.GONE
                }
            }
        }
    }
}