package com.depromeet.sloth.ui.home

import android.animation.ObjectAnimator
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
import kotlin.math.ceil

class ClassLessonAdapter(
    private val bodyType: BodyType,
    val onClick: (LessonInfoResponse) -> Unit
) : ListAdapter<LessonInfoResponse, ClassLessonAdapter.ClassLessonViewHolder>(
    ClassLessonDiffCallback
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassLessonViewHolder {
        val view = when (bodyType) {
            BodyType.NOTHING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_class_lesson_nothing, parent, false)
            BodyType.DOING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_class_lesson_doing, parent, false)
            BodyType.PLANNING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_class_lesson_planning, parent, false)
            BodyType.PASSED -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_class_lesson_passed, parent, false)
        }

        return ClassLessonViewHolder(bodyType, view)
    }

    override fun onBindViewHolder(holder: ClassLessonViewHolder, position: Int) {
        val lesson = getItem(position)
        holder.onBind(lesson)
    }

    override fun onCurrentListChanged(
        previousList: List<LessonInfoResponse?>,
        currentList: List<LessonInfoResponse?>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        notifyDataSetChanged()
    }

    inner class ClassLessonViewHolder(
        private val bodyType: BodyType,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val classLesson = itemView.findViewById<ConstraintLayout>(R.id.cl_class_lesson)
        private val classLessonCategory =
            itemView.findViewById<TextView>(R.id.tv_class_lesson_category)
        private val classLessonName = itemView.findViewById<TextView>(R.id.tv_class_lesson_name)
        private val classLessonPrice = itemView.findViewById<TextView>(R.id.tv_class_lesson_price)
        private val classLessonGoalLine =
            itemView.findViewById<View>(R.id.vw_class_lesson_goal_line)
        private val classLessonGoal = itemView.findViewById<TextView>(R.id.tv_class_lesson_goal)
        private val classLessonGoalGroup =
            itemView.findViewById<ConstraintLayout>(R.id.cl_class_goal_group)
        private val classLessonBar = itemView.findViewById<ProgressBar>(R.id.pb_class_lesson_bar)
        private val classLessonPercent =
            itemView.findViewById<TextView>(R.id.tv_class_lesson_percent)
        private val classLessonCurrentNumber =
            itemView.findViewById<TextView>(R.id.tv_class_lesson_current_number)
        private val classLessonTotalNumber =
            itemView.findViewById<TextView>(R.id.tv_class_lesson_total_number)
        private val classLessonPlanningDate =
            itemView.findViewById<TextView>(R.id.tv_class_lesson_planning_date)
        private val classLessonPassedCount =
            itemView.findViewById<TextView>(R.id.tv_class_lesson_total_count)
        private val classLessonFail = itemView.findViewById<ImageView>(R.id.iv_class_lesson_fail)
        private val registerClass = itemView.findViewById<TextView>(R.id.btn_class_lesson_register)

        fun onBind(lessonInfo: LessonInfoResponse) {
            when (bodyType) {
                BodyType.NOTHING -> {
                    registerClass.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.DOING -> {
                    val progressRate = (lessonInfo.currentProgressRate / 100.0f)
                    classLessonCurrentNumber.text = (ceil(progressRate * lessonInfo.totalNumber)).toInt().toString()
                    classLessonTotalNumber.text = lessonInfo.totalNumber.toString()
                    classLessonPercent.text = (100 - lessonInfo.currentProgressRate).toString()
                    classLessonGoal.text = (progressRate * 100).toInt().toString()
                    classLessonCategory.text = lessonInfo.categoryName
                    classLessonName.text = lessonInfo.lessonName
                    classLessonPrice.text = lessonInfo.price.toString()

                    val set = ConstraintSet()
                    set.clone(classLesson)
                    set.setHorizontalBias(classLessonGoalLine.id, progressRate)
                    set.applyTo(classLesson)
                    classLessonGoalLine.bringToFront()

                    val animation = ObjectAnimator.ofInt(
                        classLessonBar,
                        "progress",
                        classLessonBar.progress,
                        (progressRate * 10000).toInt()
                    )

                    animation.apply {
                        addListener(
                            onEnd = {
                                classLessonGoalGroup.visibility = View.VISIBLE
                                classLessonGoalGroup.bringToFront()
                            }
                        )
                        duration = 500
                        interpolator = DecelerateInterpolator()
                    }.start()

                    classLesson.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.PLANNING -> {
                    classLessonCategory.text = lessonInfo.categoryName
                    classLessonName.text = lessonInfo.lessonName
                    classLessonPrice.text = lessonInfo.price.toString()
                    classLessonPlanningDate.text = lessonInfo.startDate.split(" ")[0]

                    classLesson.setOnClickListener { onClick(lessonInfo) }
                }

                BodyType.PASSED -> {
                    classLessonCategory.text = lessonInfo.categoryName
                    classLessonName.text = lessonInfo.lessonName
                    classLessonPrice.text = lessonInfo.price.toString()
                    classLessonPassedCount.text = lessonInfo.totalNumber.toString()
                    classLessonFail.visibility =
                        if (lessonInfo.currentProgressRate / lessonInfo.goalProgressRate == 1) {
                            View.INVISIBLE
                        } else View.VISIBLE

                    classLesson.setOnClickListener { onClick(lessonInfo) }
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

object ClassLessonDiffCallback : DiffUtil.ItemCallback<LessonInfoResponse>() {
    override fun areItemsTheSame(
        oldItem: LessonInfoResponse,
        newItem: LessonInfoResponse
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: LessonInfoResponse,
        newItem: LessonInfoResponse
    ): Boolean {
        return oldItem.categoryName == newItem.categoryName
    }
}