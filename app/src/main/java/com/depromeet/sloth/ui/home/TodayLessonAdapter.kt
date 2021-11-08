package com.depromeet.sloth.ui.home

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.home.LessonResponse

class TodayLessonAdapter(
    private val bodyType: BodyType
) :
    ListAdapter<LessonResponse, TodayLessonAdapter.TodayLessonViewHolder>(
        TodayLessonDiffCallback
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayLessonViewHolder {
        val view = when(bodyType) {
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

    inner class TodayLessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nowProgress = 0
        private val todayLessonRemain = itemView.findViewById<TextView>(R.id.tv_today_lesson_remain)
        private val todayLessonCategory = itemView.findViewById<TextView>(R.id.tv_today_lesson_category)
        private val todayLessonName = itemView.findViewById<TextView>(R.id.tv_today_lesson_name)
        private val todayLessonCurrentNum = itemView.findViewById<TextView>(R.id.tv_today_lesson_current_num)
        private val todayLessonTotalNum = itemView.findViewById<TextView>(R.id.tv_today_lesson_total_num)
        private val todayLessonBar = itemView.findViewById<ProgressBar>(R.id.pb_today_lesson_bar)
        private val todayLessonMinus = itemView.findViewById<Button>(R.id.btn_today_lesson_minus)
        private val todayLessonPlus = itemView.findViewById<Button>(R.id.btn_today_lesson_plus)

        fun onBind(lesson: LessonResponse) {
            init(lesson)

            todayLessonPlus.setOnClickListener {
                updateProgress(true, lesson.goalProgressRate)
                updateText(true, lesson.goalProgressRate)
            }

            todayLessonMinus.setOnClickListener {
                updateProgress(false, lesson.goalProgressRate)
                updateText(false, lesson.goalProgressRate)
            }
        }

        private fun init(lesson: LessonResponse) {
            val remainDay = lesson.remainDay //D-day
            todayLessonCategory.text = lesson.categoryName
            todayLessonName.text = lesson.lessonName
            todayLessonCurrentNum.text = lesson.currentProgressRate.toString()
            todayLessonTotalNum.text = lesson.goalProgressRate.toString()
            todayLessonRemain.text = "D-$remainDay"
            todayLessonBar.let {
                nowProgress = lesson.currentProgressRate
                it.max = lesson.goalProgressRate * 1000
                it.progress = lesson.currentProgressRate * 1000
            }

            if(lesson.isFinished) {
                todayLessonRemain.setTextColor(Color.WHITE)
            } else {
                when (lesson.remainDay) {
                    in 0 until 10 -> todayLessonRemain.setTextColor(Color.RED)
                    else -> todayLessonRemain.setTextColor(Color.BLACK)
                }
            }
        }

        private fun updateText(isUp: Boolean, totalNum: Int) {
            if ((nowProgress < 0 && isUp.not()) || (nowProgress > totalNum && isUp)) return

            todayLessonCurrentNum.text = nowProgress.toString()
        }

        private fun updateProgress(isUp: Boolean, totalNum: Int) {
            if ((nowProgress == 0 && isUp.not()) || (nowProgress >= totalNum && isUp)) return

            when (isUp) {
                true -> nowProgress++
                false -> nowProgress--
            }

            val animation = ObjectAnimator.ofInt(todayLessonBar, "progress", todayLessonBar.progress, nowProgress * 1000)
            animation.apply {
                duration = 500
                interpolator = when (isUp) {
                    true -> DecelerateInterpolator()
                    false -> AccelerateInterpolator()
                }
            }.start()
        }
    }

    override fun onCurrentListChanged(
        previousList: List<LessonResponse?>,
        currentList: List<LessonResponse?>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        notifyDataSetChanged()
    }

    enum class BodyType {
        FINISHED,
        NOT_FINISHED
    }
}

object TodayLessonDiffCallback : DiffUtil.ItemCallback<LessonResponse>() {
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