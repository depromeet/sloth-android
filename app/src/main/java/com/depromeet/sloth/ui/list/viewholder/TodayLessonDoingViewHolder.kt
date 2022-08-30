package com.depromeet.sloth.ui.list.viewholder

import android.animation.ObjectAnimator
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemHomeTodayLessonDoingBinding
import com.depromeet.sloth.ui.list.TodayLessonAdapter

class TodayLessonDoingViewHolder(
    private val binding: ItemHomeTodayLessonDoingBinding,
    private val currentList: List<LessonTodayResponse>,
    val onClick: (TodayLessonAdapter.ClickType, LessonTodayResponse) -> Unit,
): RecyclerView.ViewHolder(binding.root) {

    private var nowProgress = 0

    fun bind(lessonToday: LessonTodayResponse) {
        itemView.apply {
            binding.apply {
                init(lessonToday)

                btnTodayLessonPlus.setOnClickListener {
                    viewTodayLessonLottie.playAnimation()
                    updateLessonCountOnServer(true, lessonToday)
                    updateProgress(true, lessonToday.untilTodayNumber)
                    updateText(true, lessonToday.untilTodayNumber)
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

        pbTodayLessonBar.let {
            it.max = lessonToday.untilTodayNumber * 1000
            it.progress = lessonToday.presentNumber * 1000
        }

        if (lessonToday.untilTodayFinished) {
            tvTodayLessonRemain.setTextColor(Color.WHITE)
        } else {
            when (lessonToday.remainDay) {
                in 0 until 10 -> tvTodayLessonRemain.setTextColor(Color.RED)
                else -> tvTodayLessonRemain.setTextColor(Color.BLACK)
            }
        }
    }

    private fun updateProgress(isUp: Boolean, totalNum: Int) = with(binding) {
        if (((nowProgress == 0) && isUp.not()) || ((nowProgress >= totalNum) && isUp)) return


        when (isUp) {
            true -> nowProgress++
            false -> nowProgress--
        }

        pbTodayLessonBar.let {
            val animation = ObjectAnimator.ofInt(
                pbTodayLessonBar,
                "progress",
                pbTodayLessonBar.progress, nowProgress * 1000
            )

            animation.apply {
                duration = 500
            }.start()
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
            onClick(TodayLessonAdapter.ClickType.CLICK_PLUS, lessonToday)
        } else {
            currentList[bindingAdapterPosition].presentNumber--
            onClick(TodayLessonAdapter.ClickType.CLICK_MINUS, lessonToday)
        }
    }

    private fun updateText(isUp: Boolean, totalNum: Int) = with(binding) {
        if (((nowProgress < 0) && isUp.not()) || ((nowProgress > totalNum) && isUp)) return

        tvTodayLessonCurrentNum.text = nowProgress.toString()
    }
}