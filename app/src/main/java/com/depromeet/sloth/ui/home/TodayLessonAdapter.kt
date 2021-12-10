package com.depromeet.sloth.ui.home

import android.animation.ObjectAnimator
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.home.TodayLessonResponse

class TodayLessonAdapter(
    private val bodyType: BodyType,
    val onClick: (ClickType, TodayLessonResponse) -> Unit
) :
    ListAdapter<TodayLessonResponse, TodayLessonAdapter.TodayLessonViewHolder>(
        TodayLessonDiffCallback
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayLessonViewHolder {
        val view = when (bodyType) {
            BodyType.NOTHING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_today_lesson_nothing, parent, false)
            BodyType.FINISHED -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_today_lesson_finished, parent, false)
            BodyType.NOT_FINISHED -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_today_lesson_not_finished, parent, false)
        }

        return TodayLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodayLessonViewHolder, position: Int) {
        val lesson = getItem(position)
        holder.onBind(lesson)
    }

    inner class TodayLessonViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private var nowProgress = 0
        private val todayLesson = itemView.findViewById<ConstraintLayout>(R.id.cl_today_lesson)
        private val todayLessonRemain = itemView.findViewById<TextView>(R.id.tv_today_lesson_remain)
        private val todayLessonCategory =
            itemView.findViewById<TextView>(R.id.tv_today_lesson_category)
        private val todayLessonName = itemView.findViewById<TextView>(R.id.tv_today_lesson_name)
        private val todayLessonCurrentNum =
            itemView.findViewById<TextView>(R.id.tv_today_lesson_current_num)
        private val todayLessonTotalNum =
            itemView.findViewById<TextView>(R.id.tv_today_lesson_total_num)
        private val todayLessonBar = itemView.findViewById<ProgressBar>(R.id.pb_today_lesson_bar)
        private val todayLessonMinus = itemView.findViewById<Button>(R.id.btn_today_lesson_minus)
        private val todayLessonPlus = itemView.findViewById<Button>(R.id.btn_today_lesson_plus)
        private val registerClass = itemView.findViewById<ConstraintLayout>(R.id.cl_today_lesson)

        fun onBind(lesson: TodayLessonResponse) {
            when (bodyType) {
                BodyType.NOTHING -> {
                    registerClass.setOnClickListener { onClick(ClickType.CLICK_NORMAL, lesson) }
                }

                BodyType.FINISHED -> {
                    init(lesson)

                    todayLesson.setOnClickListener { onClick(ClickType.CLICK_NORMAL, lesson) }
                }

                BodyType.NOT_FINISHED -> {
                    init(lesson)

                    todayLessonPlus.setOnClickListener {
                        updateLessonCountOnServer(true, lesson)
                        updateProgress(true, lesson.untilTodayNumber)
                        updateText(true, lesson.untilTodayNumber)
                    }

                    todayLessonMinus.setOnClickListener {
                        updateLessonCountOnServer(false, lesson)
                        updateProgress(false, lesson.untilTodayNumber)
                        updateText(false, lesson.untilTodayNumber)
                    }
                }
            }
        }

        private fun init(allLesson: TodayLessonResponse) {
            val remainDay = allLesson.remainDay //D-day
            todayLessonCategory.text = allLesson.categoryName
            todayLessonName.text = allLesson.lessonName
            todayLessonCurrentNum.text = allLesson.presentNumber.toString()
            todayLessonTotalNum.text = allLesson.untilTodayNumber.toString()
            "D-$remainDay".also { todayLessonRemain.text = it }
            todayLessonBar.let {
                nowProgress = allLesson.presentNumber
                it.max = allLesson.untilTodayNumber * 1000
                it.progress = allLesson.presentNumber * 1000
            }

            if (allLesson.untilTodayFinished) {
                todayLessonRemain.setTextColor(Color.WHITE)
            } else {
                when (allLesson.remainDay) {
                    in 0 until 10 -> todayLessonRemain.setTextColor(Color.RED)
                    else -> todayLessonRemain.setTextColor(Color.BLACK)
                }
            }
        }

        private fun updateText(isUp: Boolean, totalNum: Int) {
            if (((nowProgress < 0) && isUp.not()) || ((nowProgress > totalNum) && isUp)) return

            todayLessonCurrentNum.text = nowProgress.toString()
        }

        private fun updateProgress(isUp: Boolean, totalNum: Int) {
            if (((nowProgress == 0) && isUp.not()) || ((nowProgress >= totalNum) && isUp)) {
                return
            }

            when (isUp) {
                true -> nowProgress++
                false -> nowProgress--
            }

            val animation = ObjectAnimator.ofInt(
                todayLessonBar,
                "progress",
                todayLessonBar.progress, nowProgress * 1000
            )

            animation.apply {
                duration = 500
                interpolator = when (isUp) {
                    true -> DecelerateInterpolator()
                    false -> AccelerateInterpolator()
                }
            }.start()
        }

        private fun updateLessonCountOnServer(isUp: Boolean, lesson: TodayLessonResponse) {
            if (((nowProgress <= 0) && isUp.not()) || ((nowProgress >= lesson.untilTodayNumber) && isUp)) return

            if (isUp) {
                onClick(ClickType.CLICK_PLUS, lesson)
            } else {
                onClick(ClickType.CLICK_MINUS, lesson)
            }
        }

    }

    override fun onCurrentListChanged(
        previousList: List<TodayLessonResponse?>,
        currentList: List<TodayLessonResponse?>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        notifyDataSetChanged()
    }

    enum class BodyType {
        NOTHING,
        FINISHED,
        NOT_FINISHED
    }

    enum class ClickType {
        CLICK_PLUS,
        CLICK_MINUS,
        CLICK_NORMAL
    }
}

object TodayLessonDiffCallback : DiffUtil.ItemCallback<TodayLessonResponse>() {
    override fun areItemsTheSame(
        oldItem: TodayLessonResponse,
        newItem: TodayLessonResponse
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: TodayLessonResponse,
        newItem: TodayLessonResponse
    ): Boolean {
        return oldItem.categoryName == newItem.categoryName
    }
}