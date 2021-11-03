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
import com.depromeet.sloth.data.network.home.TodayLessonResponse

class TodayLessonAdapter :
    ListAdapter<TodayLessonResponse, TodayLessonAdapter.TodayLessonViewHolder>(
        TodayLessonDiffCallback
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayLessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_today_lesson, parent, false)
        return TodayLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodayLessonViewHolder, position: Int) {
        val lesson = getItem(position)
        holder.onBind(lesson)
    }

    inner class TodayLessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var nowProgress = 0
        private val todayLessonRemain = itemView.findViewById<TextView>(R.id.tv_today_lesson_remain)
        private val todayLessonBar = itemView.findViewById<ProgressBar>(R.id.pb_today_lesson_bar)
        private val todayLessonMinus = itemView.findViewById<Button>(R.id.btn_today_lesson_minus)
        private val todayLessonPlus = itemView.findViewById<Button>(R.id.btn_today_lesson_plus)
        fun onBind(lesson: TodayLessonResponse) {
            init(lesson)

            todayLessonPlus.setOnClickListener {
                updateProgress(true)
            }
            todayLessonMinus.setOnClickListener {
                updateProgress(false)
            }
        }

        private fun init(lesson: TodayLessonResponse) {
            val remainDay = lesson.remainDay //D-day
            todayLessonRemain.text = "D-$remainDay"
            when (lesson.remainDay) {
                in 0 until 10 -> todayLessonRemain.setTextColor(Color.RED)
                else -> todayLessonRemain.setTextColor(Color.BLACK)
            }
        }

        private fun updateProgress(isUp: Boolean) {
            if ((nowProgress == 0 && isUp.not()) || (nowProgress >= 10 && isUp)) return

            when (isUp) {
                true -> nowProgress++
                false -> nowProgress--
            }

            val animation = ObjectAnimator.ofInt(todayLessonBar, "progress", todayLessonBar.progress, nowProgress * 10 * 100)
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
        previousList: List<TodayLessonResponse?>,
        currentList: List<TodayLessonResponse?>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        notifyDataSetChanged()
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