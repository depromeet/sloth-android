package com.depromeet.sloth.presentation.adapter.viewholder

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemTodayLessonDoingBinding
import com.depromeet.sloth.presentation.adapter.TodayLessonAdapter

class TodayLessonDoingViewHolder(
    private val context: Context,
    private val binding: ItemTodayLessonDoingBinding,
    private val currentList: List<LessonTodayResponse>,
    private val onClick: (TodayLessonAdapter.ClickType, LessonTodayResponse, Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var nowProgress = 0

    fun bind(lessonToday: LessonTodayResponse) {
        itemView.apply {
            binding.apply {
                init(lessonToday)

                btnTodayLessonPlus.setOnClickListener {
                    viewTodayLessonLottie.playAnimation()
                    // onBoarding 인 경우 api 호출 되면 안됨
                    val isOnBoarding =
                        tvTodayLessonCategory.text == context.getString(R.string.tutorial)
                    if (isOnBoarding) {
                        onClick(TodayLessonAdapter.ClickType.CLICK_PLUS, lessonToday, DELAY_TIME)
                    } else {
                        updateLessonCountOnServer(true, lessonToday)
                    }
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
        tvTodayLessonRemain.text =
            if (lessonToday.remainDay == 0) "D-Day" else "D-${lessonToday.remainDay}"
        tvTodayLessonCategory.text = lessonToday.categoryName
        if (lessonToday.siteName.isNotEmpty()) {
            tvTodayLessonSite.text = lessonToday.siteName
        } else {
            tvTodayLessonSite.visibility = View.GONE
        }
        tvTodayLessonName.text = lessonToday.lessonName
        tvTodayLessonCurrentNum.text = lessonToday.presentNumber.toString()
        tvTodayLessonTotalNum.text = lessonToday.untilTodayNumber.toString()
        nowProgress = lessonToday.presentNumber

        pbTodayLessonBar.let {
            it.max = lessonToday.untilTodayNumber * 1000
            it.progress = lessonToday.presentNumber * 1000
        }
        tvTodayLessonRemain.setTextColor(context.resources.getColor(R.color.primary_400, null))
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
                duration = DELAY_TIME
            }.start()
        }
    }

    private fun updateLessonCountOnServer(
        isUp: Boolean,
        lessonToday: LessonTodayResponse
    ) = with(binding) {
        //if (((nowProgress <= 0) && isUp.not()) || ((nowProgress >= lessonToday.untilTodayNumber) && isUp)) return
        val isOutOfRange =
            ((nowProgress <= 0) && isUp.not()) || ((lessonToday.presentNumber == lessonToday.totalNumber) && isUp)
        if (isOutOfRange) return

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
        const val DELAY_TIME = 350L
    }
}