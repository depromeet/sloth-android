package com.depromeet.sloth.ui.home

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.home.LessonResponse

class ClassLessonAdapter(
    private val bodyType: BodyType
) : ListAdapter<LessonResponse, ClassLessonAdapter.ClassLessonViewHolder>(
    ClassLessonDiffCallback
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassLessonViewHolder {
        val view = when (bodyType) {
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
        previousList: List<LessonResponse?>,
        currentList: List<LessonResponse?>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        notifyDataSetChanged()
    }

    inner class ClassLessonViewHolder(
        private val bodyType: BodyType,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val classLessonLayout =
            itemView.findViewById<ConstraintLayout>(R.id.cl_class_lesson_layoutc)
        private val classLessonGoalLine =
            itemView.findViewById<View>(R.id.vw_class_lesson_goal_line)
        private val classLessonGoalText =
            itemView.findViewById<TextView>(R.id.tv_class_lesson_goal_text)
        private val classLessonBar = itemView.findViewById<ProgressBar>(R.id.pb_class_lesson_bar)

        fun onBind(lesson: LessonResponse) {
            when (bodyType) {
                BodyType.DOING -> {
                    val set = ConstraintSet()
                    set.clone(classLessonLayout)
                    set.setHorizontalBias(classLessonGoalLine.id, 0.4f)
                    set.applyTo(classLessonLayout)
                    classLessonGoalLine.bringToFront()

                    classLessonBar.max = 10000
                    val animation = ObjectAnimator.ofInt(
                        classLessonBar,
                        "progress",
                        classLessonBar.progress,
                        4000
                    )
                    animation.apply {
                        addListener(
                            onEnd = {
                                classLessonGoalText.visibility = View.VISIBLE
                                classLessonGoalText.bringToFront()
                            }
                        )
                        duration = 500
                        interpolator = DecelerateInterpolator()
                    }.start()
                }
            }
        }
    }

    enum class BodyType {
        DOING,
        PLANNING,
        PASSED
    }
}

object ClassLessonDiffCallback : DiffUtil.ItemCallback<LessonResponse>() {
    override fun areItemsTheSame(
        oldItem: LessonResponse,
        newItem: LessonResponse
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: LessonResponse,
        newItem: LessonResponse
    ): Boolean {
        return oldItem.categoryName == newItem.categoryName
    }
}