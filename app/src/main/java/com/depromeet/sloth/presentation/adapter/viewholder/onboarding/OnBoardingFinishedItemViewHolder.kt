package com.depromeet.sloth.presentation.adapter.viewholder.onboarding

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.databinding.ItemOnBoardingFinishedBinding

class OnBoardingFinishedItemViewHolder(val binding: ItemOnBoardingFinishedBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(onBoardingItem: TodayLessonResponse) {
        itemView.apply {
            binding.apply {
                tvTodayLessonCurrentNumber.text = onBoardingItem.presentNumber.toString()
                tvTodayLessonTotalNumber.text = onBoardingItem.untilTodayNumber.toString()

                if(onBoardingItem.presentNumber == onBoardingItem.totalNumber) {
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