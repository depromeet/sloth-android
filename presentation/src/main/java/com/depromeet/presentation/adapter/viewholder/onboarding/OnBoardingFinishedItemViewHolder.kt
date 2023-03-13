package com.depromeet.presentation.adapter.viewholder.onboarding

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.ItemOnBoardingFinishedBinding
import com.depromeet.presentation.model.TodayLesson


class OnBoardingFinishedItemViewHolder(val binding: ItemOnBoardingFinishedBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(onBoardingItem: TodayLesson) {
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