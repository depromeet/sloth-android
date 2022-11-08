package com.depromeet.sloth.ui.list.viewholder

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.ItemHomeLessonListDoingBinding
import com.depromeet.sloth.extensions.changeDecimalFormat
import kotlin.math.ceil

class LessonListDoingViewHolder(
    private val binding: ItemHomeLessonListDoingBinding,
    val onClick: (LessonAllResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lessonInfo: LessonAllResponse) {
        itemView.apply {
            binding.apply {
                val progressRate = (lessonInfo.currentProgressRate / 100.0f)
                tvLessonListCurrentNumber.text =
                    (ceil(progressRate * lessonInfo.totalNumber)).toInt().toString()
                tvLessonListTotalNumber.text = lessonInfo.totalNumber.toString()
                tvLessonListPercent.text = (100 - lessonInfo.currentProgressRate).toString()
                tvLessonListGoal.text = (progressRate * 100).toInt().toString()
                tvLessonListRemain.text =
                    if (lessonInfo.remainDay == 0) "D-Day" else "D-${lessonInfo.remainDay}"
                tvLessonListCategory.text = lessonInfo.categoryName
                tvLessonListSite.text = lessonInfo.siteName
                tvLessonListName.text = lessonInfo.lessonName
                tvLessonListPrice.text = changeDecimalFormat(lessonInfo.price)

                val set = ConstraintSet()
                set.clone(clLessonList)
                set.setHorizontalBias(vwLessonListCurrentLine.id, progressRate)
                set.setHorizontalBias(
                    vwLessonListGoalLine.id,
                    lessonInfo.goalProgressRate / 100.0f
                )
                set.applyTo(clLessonList)
                vwLessonListGoalLine.bringToFront()
                vwLessonListCurrentLine.bringToFront()

                val animation = ObjectAnimator.ofInt(
                    pbLessonListBar,
                    "progress",
                    pbLessonListBar.progress,
                    (progressRate * 10000).toInt()
                )

                animation.apply {
                    addListener(
                        onEnd = {
                            clLessonListGoalGroup.visibility = View.VISIBLE
                            clLessonListGoalGroup.bringToFront()
                        }
                    )
                    duration = 500
                    interpolator = DecelerateInterpolator()
                }.start()


                clLessonList.setOnClickListener { onClick(lessonInfo) }
            }
        }
    }
}