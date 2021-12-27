package com.depromeet.sloth.ui.list

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.home.LessonInfoResponse
import java.text.DecimalFormat
import kotlin.math.ceil

class LessonListAdapter(
    private val bodyType: BodyType,
    val onClick: (LessonInfoResponse) -> Unit
) : ListAdapter<LessonInfoResponse, LessonListAdapter.LessonListViewHolder>(LessonListDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LessonListViewHolder {
        val view = when (bodyType) {
            BodyType.NOTHING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_lesson_list_nothing, parent, false)
            BodyType.DOING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_lesson_list_doing, parent, false)
            BodyType.PLANNING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_lesson_list_planning, parent, false)
            BodyType.PASSED -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_lesson_list_passed, parent, false)
        }

        return LessonListViewHolder(bodyType, view)
    }

    override fun onBindViewHolder(
        holder: LessonListViewHolder,
        position: Int
    ) {
        val lesson = getItem(position)
        holder.onBind(lesson)
    }

    private fun changeDecimalFormat(data: Int): String {
        val df = DecimalFormat("#,###")

        return df.format(data)
    }

    inner class LessonListViewHolder(
        private val bodyType: BodyType,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val lessonList = itemView.findViewById<ConstraintLayout>(R.id.cl_lesson_list)
        private val lessonListRemain = itemView.findViewById<TextView>(R.id.tv_lesson_list_remain)
        private val lessonListCategory =
            itemView.findViewById<TextView>(R.id.tv_lesson_list_category)
        private val lessonListSite =
            itemView.findViewById<TextView>(R.id.tv_lesson_list_site)
        private val lessonListName = itemView.findViewById<TextView>(R.id.tv_lesson_list_name)
        private val lessonListPrice = itemView.findViewById<TextView>(R.id.tv_lesson_list_price)
        private val lessonListCurrentLine =
            itemView.findViewById<View>(R.id.vw_lesson_list_current_line)
        private val lessonListGoalLine =
            itemView.findViewById<View>(R.id.vw_lesson_list_goal_line)
        private val lessonListGoal = itemView.findViewById<TextView>(R.id.tv_lesson_list_goal)
        private val lessonListGoalGroup =
            itemView.findViewById<ConstraintLayout>(R.id.cl_lesson_list_goal_group)
        private val lessonListBar = itemView.findViewById<ProgressBar>(R.id.pb_lesson_list_bar)
        private val lessonListPercent =
            itemView.findViewById<TextView>(R.id.tv_lesson_list_percent)
        private val lessonListCurrentNumber =
            itemView.findViewById<TextView>(R.id.tv_lesson_list_current_number)
        private val lessonListTotalNumber =
            itemView.findViewById<TextView>(R.id.tv_lesson_list_total_number)
        private val lessonListPlanningDate =
            itemView.findViewById<TextView>(R.id.tv_lesson_list_planning_date)
        private val lessonListPassedCount =
            itemView.findViewById<TextView>(R.id.tv_lesson_list_total_count)
        private val lessonListFail = itemView.findViewById<ImageView>(R.id.iv_lesson_list_fail)
        private val registerLesson = itemView.findViewById<TextView>(R.id.btn_lesson_list_register)

        @SuppressLint("SetTextI18n")
        fun onBind(lessonInfo: LessonInfoResponse) {
            when (bodyType) {
                BodyType.NOTHING -> {
                    registerLesson.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.DOING -> {
                    val progressRate = (lessonInfo.currentProgressRate / 100.0f)
                    lessonListCurrentNumber.text =
                        (ceil(progressRate * lessonInfo.totalNumber)).toInt().toString()
                    lessonListTotalNumber.text = lessonInfo.totalNumber.toString()
                    lessonListPercent.text = (100 - lessonInfo.currentProgressRate).toString()
                    lessonListGoal.text = (progressRate * 100).toInt().toString()
                    lessonListRemain.text = "D-${lessonInfo.remainDay}"
                    lessonListCategory.text = lessonInfo.categoryName
                    lessonListSite.text = lessonInfo.siteName
                    lessonListName.text = lessonInfo.lessonName
                    lessonListPrice.text = changeDecimalFormat(lessonInfo.price)

                    val set = ConstraintSet()
                    set.clone(lessonList)
                    set.setHorizontalBias(lessonListCurrentLine.id, progressRate)
                    set.setHorizontalBias(
                        lessonListGoalLine.id,
                        lessonInfo.goalProgressRate / 100.0f
                    )
                    set.applyTo(lessonList)
                    lessonListGoalLine.bringToFront()
                    lessonListCurrentLine.bringToFront()

                    val animation = ObjectAnimator.ofInt(
                        lessonListBar,
                        "progress",
                        lessonListBar.progress,
                        (progressRate * 10000).toInt()
                    )

                    animation.apply {
                        addListener(
                            onEnd = {
                                lessonListGoalGroup.visibility = View.VISIBLE
                                lessonListGoalGroup.bringToFront()
                            }
                        )
                        duration = 500
                        interpolator = DecelerateInterpolator()
                    }.start()


                    lessonList.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.PLANNING -> {
                    lessonListCategory.text = lessonInfo.categoryName
                    lessonListSite.text = lessonInfo.siteName
                    lessonListName.text = lessonInfo.lessonName
                    lessonListPrice.text = changeDecimalFormat(lessonInfo.price)
                    lessonListPlanningDate.text = lessonInfo.startDate.replace("-", ".")

                    lessonList.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.PASSED -> {
                    lessonListCategory.text = lessonInfo.categoryName
                    lessonListSite.text = lessonInfo.siteName
                    lessonListName.text = lessonInfo.lessonName
                    lessonListPrice.text = changeDecimalFormat(lessonInfo.price)
                    lessonListPassedCount.text = lessonInfo.totalNumber.toString()
                    val progressRate = (lessonInfo.currentProgressRate / 100.0f)
                    val isComplete =
                        (ceil(progressRate * lessonInfo.totalNumber)).toInt() == lessonInfo.totalNumber
                    lessonListFail.visibility = if (isComplete) {
                        View.INVISIBLE
                    } else View.VISIBLE

                    lessonList.setOnClickListener { onClick(lessonInfo) }
                }
            }
        }
    }

    enum class BodyType {
        NOTHING,
        DOING,
        PLANNING,
        PASSED
    }
}

object LessonListDiffCallback : DiffUtil.ItemCallback<LessonInfoResponse>() {
    override fun areItemsTheSame(
        oldItem: LessonInfoResponse,
        newItem: LessonInfoResponse
    ): Boolean {
        return oldItem.lessonId == newItem.lessonId
    }

    override fun areContentsTheSame(
        oldItem: LessonInfoResponse,
        newItem: LessonInfoResponse
    ): Boolean {
        return oldItem.categoryName == newItem.categoryName &&
                oldItem.lessonName == newItem.lessonName &&
                oldItem.startDate == newItem.startDate &&
                oldItem.endDate == newItem.endDate &&
                oldItem.siteName == newItem.siteName
    }
}