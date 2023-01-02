package com.depromeet.sloth.ui.adapter.viewholder

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemHomeTodayLessonFinishedBinding
import com.depromeet.sloth.ui.adapter.TodayLessonAdapter

class TodayLessonFinishedViewHolder(
    private val binding: ItemHomeTodayLessonFinishedBinding,
    private val currentList: List<LessonTodayResponse>,
    val onClick: (TodayLessonAdapter.ClickType, LessonTodayResponse, Long) -> Unit,
): RecyclerView.ViewHolder(binding.root) {

    private var nowProgress = 0

    fun bind(lessonToday: LessonTodayResponse) {
        itemView.apply {
            binding.apply {
                init(lessonToday)

                if(lessonToday.totalNumber == lessonToday.presentNumber) {
                    clTodayFinishedTop.setBackgroundResource(R.drawable.bg_today_lesson_finished_top)
                    clTodayFinishedBottom.visibility = View.VISIBLE
                } else {
                    clTodayFinishedTop.setBackgroundResource(R.drawable.bg_today_lesson_not_finished_top)
                    clTodayFinishedBottom.visibility = View.GONE
                }

                clTodayFinishedBottom.setOnClickListener {
                    onClick(TodayLessonAdapter.ClickType.CLICK_COMPLETE, lessonToday, DELAY_TIME)
                }

                btnTodayLessonPlus.setOnClickListener {
                    updateLessonCountOnServer(true, lessonToday)
                    updateProgress(true, lessonToday.totalNumber)
                    updateText(true, lessonToday.totalNumber)
                }

                btnTodayLessonMinus.setOnClickListener {
                    updateLessonCountOnServer(false, lessonToday)
                    updateProgress(false, lessonToday.untilTodayNumber)
                    updateText(false, lessonToday.untilTodayNumber)
                }
            }
        }
    }

    private fun init(lessonToday: LessonTodayResponse) = with(binding) {
        tvTodayLessonRemain.text = if (lessonToday.remainDay == 0) "D-Day" else "D-${lessonToday.remainDay}"
        tvTodayLessonCategory.text = lessonToday.categoryName
        tvTodayLessonSite.text = lessonToday.siteName
        tvTodayLessonName.text = lessonToday.lessonName
        tvTodayLessonCurrentNum.text = lessonToday.presentNumber.toString()
        tvTodayLessonTotalNum.text = lessonToday.untilTodayNumber.toString()
        nowProgress = lessonToday.presentNumber

        if (lessonToday.untilTodayFinished) {
            tvTodayLessonRemain.setTextColor(Color.WHITE)
        } else {
            when (lessonToday.remainDay) {
                in 0 until 10 -> tvTodayLessonRemain.setTextColor(Color.RED)
                else -> tvTodayLessonRemain.setTextColor(Color.BLACK)
            }
        }
    }

    private fun updateProgress(isUp: Boolean, totalNum: Int) {
        if (((nowProgress == 0) && isUp.not()) || ((nowProgress >= totalNum) && isUp)) return

        when (isUp) {
            true -> nowProgress++
            false -> nowProgress--
        }
    }

    private fun updateLessonCountOnServer(
        isUp: Boolean,
        lessonToday: LessonTodayResponse
    ) = with(binding) {
        //if (((nowProgress <= 0) && isUp.not()) || ((nowProgress >= lessonToday.untilTodayNumber) && isUp)) return
        if (((nowProgress <= 0) && isUp.not()) || ((lessonToday.presentNumber == lessonToday.totalNumber) && isUp)) return

        if (isUp) {
            currentList[bindingAdapterPosition].presentNumber++
            onClick(TodayLessonAdapter.ClickType.CLICK_PLUS, lessonToday, DELAY_TIME)
        } else {
            currentList[bindingAdapterPosition].presentNumber--
            onClick(TodayLessonAdapter.ClickType.CLICK_MINUS, lessonToday, DELAY_TIME)
        }
    }

    private fun updateText(isUp: Boolean, totalNum: Int) = with(binding) {
        if (((nowProgress < 0) && isUp.not()) || ((nowProgress > totalNum) && isUp)) return

        tvTodayLessonCurrentNum.text = nowProgress.toString()
    }

    companion object {
        const val DELAY_TIME = 200L
    }
}