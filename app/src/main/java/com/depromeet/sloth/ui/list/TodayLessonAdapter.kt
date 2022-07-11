package com.depromeet.sloth.ui.list

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.airbnb.lottie.LottieAnimationView
import com.depromeet.sloth.ui.custom.ArcProgressBar


class TodayLessonAdapter(
    private val bodyType: BodyType,
    val onClick: (ClickType, LessonTodayResponse) -> Unit,
) : ListAdapter<LessonTodayResponse, TodayLessonAdapter.TodayLessonViewHolder>(
    TodayLessonDiffCallback
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TodayLessonViewHolder {
        val view = when (bodyType) {
            BodyType.NOTHING -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_today_lesson_nothing, parent, false)
            BodyType.FINISHED -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_today_lesson_finished, parent, false)
            BodyType.NOT_FINISHED -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_today_lesson_doing, parent, false)
        }

        return TodayLessonViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TodayLessonViewHolder,
        position: Int,
    ) {
        val lesson = getItem(position)
        holder.onBind(lesson)
    }

    inner class TodayLessonViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private var nowProgress = 0
        private val clTodayLesson = itemView.findViewById<ConstraintLayout>(R.id.cl_today_lesson)
        private val clTodayFinishedTop = itemView.findViewById<ConstraintLayout>(R.id.cl_today_finished_top)
        private val clTodayFinishedBottom = itemView.findViewById<ConstraintLayout>(R.id.cl_today_finished_bottom)
        private val tvTodayLessonRemain = itemView.findViewById<TextView>(R.id.tv_today_lesson_remain)
        private val tvTodayLessonCategory = itemView.findViewById<TextView>(R.id.tv_today_lesson_category)
        private val tvTodayLessonSite = itemView.findViewById<TextView>(R.id.tv_today_lesson_site)
        private val tvTodayLessonName = itemView.findViewById<TextView>(R.id.tv_today_lesson_name)
        private val tvTodayLessonCurrentNum = itemView.findViewById<TextView>(R.id.tv_today_lesson_current_num)
        private val tvTodayLessonTotalNum = itemView.findViewById<TextView>(R.id.tv_today_lesson_total_num)
        private val pbTodayLessonBar = itemView.findViewById<ArcProgressBar>(R.id.pb_today_lesson_bar)
        private val btnTodayLessonMinus = itemView.findViewById<Button>(R.id.btn_today_lesson_minus)
        private val btnTodayLessonPlus = itemView.findViewById<Button>(R.id.btn_today_lesson_plus)
        private val viewTodayLessonLottie = itemView.findViewById<LottieAnimationView>(R.id.view_today_lesson_lottie)

        fun onBind(lessonToday: LessonTodayResponse) {
            when (bodyType) {
                BodyType.NOTHING -> {
                    clTodayLesson.setOnClickListener {
                        onClick(ClickType.CLICK_NORMAL, lessonToday)
                    }
                }

                BodyType.FINISHED -> {
                    init(lessonToday)

                    if(lessonToday.totalNumber == lessonToday.presentNumber) {
                        clTodayFinishedTop.setBackgroundResource(R.drawable.bg_home_today_finished_top)
                        clTodayFinishedBottom.visibility = View.VISIBLE
                    } else {
                        clTodayFinishedTop.setBackgroundResource(R.drawable.bg_home_today_not_finished_top)
                        clTodayFinishedBottom.visibility = View.GONE
                    }

                    clTodayFinishedBottom.setOnClickListener {
                        onClick(ClickType.CLICK_COMPLETE, lessonToday)
                    }

                    btnTodayLessonPlus.setOnClickListener {
                        updateLessonCountOnServer(true, lessonToday)
                        updateProgress(true, lessonToday.totalNumber)
                        updateText(true, lessonToday.totalNumber)
                    }

                    btnTodayLessonMinus.setOnClickListener {
                        updateLessonCountOnServer(false, lessonToday)
                        updateProgress(false, lessonToday.untilTodayNumber)
                        updateText(false, lessonToday.untilTodayNumber)
                    }
                }

                BodyType.NOT_FINISHED -> {
                    init(lessonToday)

                    btnTodayLessonPlus.setOnClickListener {
                        viewTodayLessonLottie.playAnimation()
                        updateLessonCountOnServer(true, lessonToday)
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

        private fun init(lessonToday: LessonTodayResponse) {
            tvTodayLessonRemain.text = if (lessonToday.remainDay == 0) "D-Day" else "D-${lessonToday.remainDay}"
            tvTodayLessonCategory.text = lessonToday.categoryName
            tvTodayLessonSite.text = lessonToday.siteName
            tvTodayLessonName.text = lessonToday.lessonName
            tvTodayLessonCurrentNum.text = lessonToday.presentNumber.toString()
            tvTodayLessonTotalNum.text = lessonToday.untilTodayNumber.toString()
            nowProgress = lessonToday.presentNumber

//            pbTodayLessonBar.let {
//                it.max = allLessonToday.untilTodayNumber * 1000
//                it.progress = allLessonToday.presentNumber * 1000
//            }

            pbTodayLessonBar?.let {
                it.max = lessonToday.untilTodayNumber * 1000
                it.progress = lessonToday.presentNumber * 1000
            }

            if (lessonToday.untilTodayFinished) {
                tvTodayLessonRemain.setTextColor(Color.WHITE)
            } else {
                when (lessonToday.remainDay) {
                    in 0 until 10 -> tvTodayLessonRemain.setTextColor(Color.RED)
                    else -> tvTodayLessonRemain.setTextColor(Color.BLACK)
                }
            }
        }

        private fun updateText(isUp: Boolean, totalNum: Int) {
            if (((nowProgress < 0) && isUp.not()) || ((nowProgress > totalNum) && isUp)) return

            tvTodayLessonCurrentNum.text = nowProgress.toString()
        }

        private fun updateProgress(isUp: Boolean, totalNum: Int) {
            if (((nowProgress == 0) && isUp.not()) || ((nowProgress >= totalNum) && isUp)) {
                return
            }

            when (isUp) {
                true -> nowProgress++
                false -> nowProgress--
            }

            pbTodayLessonBar?.let {
                val animationTest = ObjectAnimator.ofInt(
                    pbTodayLessonBar,
                    "progress",
                    pbTodayLessonBar.progress, nowProgress * 1000
                )

                animationTest.apply {
                    duration = 500
                }.start()
            }

//            val animation = ObjectAnimator.ofInt(
//                pbTodayLessonBar,
//                "progress",
//                pbTodayLessonBar.progress, nowProgress * 1000
//            )
//
//            animation.apply {
//                duration = 500
//            }.start()
        }

        private fun updateLessonCountOnServer(
            isUp: Boolean,
            lessonToday: LessonTodayResponse
        ) {
            //if (((nowProgress <= 0) && isUp.not()) || ((nowProgress >= lessonToday.untilTodayNumber) && isUp)) return
            if (((nowProgress <= 0) && isUp.not()) || ((lessonToday.presentNumber == lessonToday.totalNumber) && isUp)) return

            if (isUp) {
                currentList[bindingAdapterPosition].presentNumber++
                onClick(ClickType.CLICK_PLUS, lessonToday)
            } else {
                currentList[bindingAdapterPosition].presentNumber--
                onClick(ClickType.CLICK_MINUS, lessonToday)
            }
        }
    }

    enum class BodyType {
        NOTHING,
        FINISHED,
        NOT_FINISHED
    }

    enum class ClickType(val value: Int) {
        CLICK_PLUS(1),
        CLICK_MINUS(-1),
        CLICK_NORMAL(0),
        CLICK_COMPLETE(2)
    }
}

object TodayLessonDiffCallback : DiffUtil.ItemCallback<LessonTodayResponse>() {
    override fun areItemsTheSame(
        oldItem: LessonTodayResponse,
        newItem: LessonTodayResponse,
    ): Boolean {
        return oldItem.lessonId == newItem.lessonId
    }

    override fun areContentsTheSame(
        oldItem: LessonTodayResponse,
        newItem: LessonTodayResponse,
    ): Boolean {
        return oldItem.categoryName == newItem.categoryName &&
                oldItem.lessonName == newItem.lessonName &&
                oldItem.siteName == newItem.siteName
    }
}