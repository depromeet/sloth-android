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
import com.depromeet.sloth.data.network.lesson.LessonAllResponse
import java.text.DecimalFormat
import kotlin.math.ceil

class LessonListAdapter(
    private val bodyType: BodyType,
    val onClick: (LessonAllResponse) -> Unit
) : ListAdapter<LessonAllResponse, LessonListAdapter.LessonListViewHolder>(LessonListDiffCallback) {
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
        private val clLessonList = itemView.findViewById<ConstraintLayout>(R.id.cl_lesson_list)
        private val tvLessonListRemain = itemView.findViewById<TextView>(R.id.tv_lesson_list_remain)
        private val tvLessonListCategory = itemView.findViewById<TextView>(R.id.tv_lesson_list_category)
        private val tvLessonListSite = itemView.findViewById<TextView>(R.id.tv_lesson_list_site)
        private val tvLessonListName = itemView.findViewById<TextView>(R.id.tv_lesson_list_name)
        private val tvLessonListPrice = itemView.findViewById<TextView>(R.id.tv_lesson_list_price)
        private val vwLessonListCurrentLine = itemView.findViewById<View>(R.id.vw_lesson_list_current_line)
        private val vwLessonListGoalLine = itemView.findViewById<View>(R.id.vw_lesson_list_goal_line)
        private val tvLessonListGoal = itemView.findViewById<TextView>(R.id.tv_lesson_list_goal)
        private val clLessonListGoalGroup = itemView.findViewById<ConstraintLayout>(R.id.cl_lesson_list_goal_group)
        private val pbLessonListBar = itemView.findViewById<ProgressBar>(R.id.pb_lesson_list_bar)
        private val tvLessonListPercent = itemView.findViewById<TextView>(R.id.tv_lesson_list_percent)
        private val tvLessonListCurrentNumber = itemView.findViewById<TextView>(R.id.tv_lesson_list_current_number)
        private val tvLessonListTotalNumber = itemView.findViewById<TextView>(R.id.tv_lesson_list_total_number)
        private val tvLessonListPlanningDate = itemView.findViewById<TextView>(R.id.tv_lesson_list_planning_date)
        private val tvLessonListPassedCount = itemView.findViewById<TextView>(R.id.tv_lesson_list_total_count)
        private val ivLessonListFail = itemView.findViewById<ImageView>(R.id.iv_lesson_list_fail)
        private val btnRegisterLesson = itemView.findViewById<TextView>(R.id.btn_lesson_list_register)

        @SuppressLint("SetTextI18n")
        fun onBind(lessonInfo: LessonAllResponse) {
            when (bodyType) {
                BodyType.NOTHING -> {
                    btnRegisterLesson.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.DOING -> {
                    val progressRate = (lessonInfo.currentProgressRate / 100.0f)
                    tvLessonListCurrentNumber.text = (ceil(progressRate * lessonInfo.totalNumber)).toInt().toString()
                    tvLessonListTotalNumber.text = lessonInfo.totalNumber.toString()
                    tvLessonListPercent.text = (100 - lessonInfo.currentProgressRate).toString()
                    tvLessonListGoal.text = (progressRate * 100).toInt().toString()
                    tvLessonListRemain.text = "D-${lessonInfo.remainDay}"
                    tvLessonListCategory.text = lessonInfo.categoryName
                    tvLessonListSite.text = lessonInfo.siteName
                    tvLessonListName.text = lessonInfo.lessonName
                    tvLessonListPrice.text = changeDecimalFormat(lessonInfo.price)

                    val set = ConstraintSet()
                    set.clone(clLessonList)
                    set.setHorizontalBias(vwLessonListCurrentLine.id, progressRate)
                    set.setHorizontalBias(vwLessonListGoalLine.id, lessonInfo.goalProgressRate / 100.0f)
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

                BodyType.PLANNING -> {
                    tvLessonListCategory.text = lessonInfo.categoryName
                    tvLessonListSite.text = lessonInfo.siteName
                    tvLessonListName.text = lessonInfo.lessonName
                    tvLessonListPrice.text = changeDecimalFormat(lessonInfo.price)
                    tvLessonListPlanningDate.text = lessonInfo.startDate.replace("-", ".")

                    clLessonList.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.PASSED -> {
                    tvLessonListCategory.text = lessonInfo.categoryName
                    tvLessonListSite.text = lessonInfo.siteName
                    tvLessonListName.text = lessonInfo.lessonName
                    tvLessonListPrice.text = changeDecimalFormat(lessonInfo.price)
                    tvLessonListPassedCount.text = lessonInfo.totalNumber.toString()
                    val progressRate = (lessonInfo.currentProgressRate / 100.0f)
                    val isComplete =
                        (ceil(progressRate * lessonInfo.totalNumber)).toInt() == lessonInfo.totalNumber
                    ivLessonListFail.visibility = if (isComplete) {
                        View.INVISIBLE
                    } else View.VISIBLE

                    clLessonList.setOnClickListener { onClick(lessonInfo) }
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

object LessonListDiffCallback : DiffUtil.ItemCallback<LessonAllResponse>() {
    override fun areItemsTheSame(
        oldItem: LessonAllResponse,
        newItem: LessonAllResponse
    ): Boolean {
        return oldItem.lessonId == newItem.lessonId
    }

    override fun areContentsTheSame(
        oldItem: LessonAllResponse,
        newItem: LessonAllResponse
    ): Boolean {
        return oldItem.categoryName == newItem.categoryName &&
                oldItem.lessonName == newItem.lessonName &&
                oldItem.startDate == newItem.startDate &&
                oldItem.endDate == newItem.endDate &&
                oldItem.siteName == newItem.siteName
    }
}