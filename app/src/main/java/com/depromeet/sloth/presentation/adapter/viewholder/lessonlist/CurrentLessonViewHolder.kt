package com.depromeet.sloth.presentation.adapter.viewholder.lessonlist

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.ItemLessonListCurrentBinding
import com.depromeet.sloth.extensions.changeDecimalFormat
import kotlin.math.ceil

class CurrentLessonViewHolder(val binding: ItemLessonListCurrentBinding, ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(lesson: LessonListResponse) {
        itemView.apply {
            binding.apply {
                val progressRate = (lesson.currentProgressRate / 100.0f)
                tvLessonListCurrentNumber.text =
                    (ceil(progressRate * lesson.totalNumber)).toInt().toString()
                tvLessonListTotalNumber.text = lesson.totalNumber.toString()
                tvLessonListPercent.text = (100 - lesson.currentProgressRate).toString()
                tvLessonListGoal.text = (progressRate * 100).toInt().toString()
                tvLessonListRemain.text =
                    if (lesson.remainDay == 0) "D-Day" else "D-${lesson.remainDay}"
                tvLessonListCategory.text = lesson.categoryName
                tvLessonListSite.text = lesson.siteName
                tvLessonListName.text = lesson.lessonName
                tvLessonListPrice.text = changeDecimalFormat(lesson.price)

                val set = ConstraintSet()
                set.clone(clLessonList)
                set.setHorizontalBias(vwLessonListCurrentLine.id, progressRate)
                set.setHorizontalBias(
                    vwLessonListGoalLine.id,
                    lesson.goalProgressRate / 100.0f
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
            }
        }
    }
}