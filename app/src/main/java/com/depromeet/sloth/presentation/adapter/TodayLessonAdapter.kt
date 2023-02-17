package com.depromeet.sloth.presentation.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.databinding.*
import com.depromeet.sloth.presentation.adapter.viewholder.*
import com.depromeet.sloth.presentation.screen.LessonUiModel
import com.depromeet.sloth.presentation.screen.todaylesson.LessonItemClickListener
import com.depromeet.sloth.util.setOnSingleClickListener


class TodayLessonAdapter(
    private val clickListener: LessonItemClickListener
) : ListAdapter<LessonUiModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<LessonUiModel>() {
        override fun areItemsTheSame(oldItem: LessonUiModel, newItem: LessonUiModel): Boolean {
            return (oldItem is LessonUiModel.EmptyLesson && newItem is LessonUiModel.EmptyLesson) ||
                    (oldItem is LessonUiModel.DoingLesson && newItem is LessonUiModel.DoingLesson && oldItem.todayLesson.lessonId == newItem.todayLesson.lessonId) ||
                    (oldItem is LessonUiModel.FinishedLesson && newItem is LessonUiModel.FinishedLesson && oldItem.todayLesson.lessonId == newItem.todayLesson.lessonId)
        }

        override fun areContentsTheSame(oldItem: LessonUiModel, newItem: LessonUiModel) =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_lesson_header -> LessonHeaderViewHolder(
                ItemLessonHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_today_lesson_empty -> TodayEmptyLessonViewHolder(
                ItemTodayLessonEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_today_lesson_doing -> TodayDoingLessonViewHolder(
                ItemTodayLessonDoingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_today_lesson_finished -> TodayFinishedLessonViewHolder(
                ItemTodayLessonFinishedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val uiModel = getItem(position)) {
            is LessonUiModel.LessonHeader ->
                (holder as LessonHeaderViewHolder).bind(uiModel.headerType, null)

            is LessonUiModel.EmptyLesson -> (holder as TodayEmptyLessonViewHolder).apply {
                binding.clTodayLesson.setOnSingleClickListener { clickListener.onClick() }
            }
            is LessonUiModel.DoingLesson -> (holder as TodayDoingLessonViewHolder).apply {
                bind(uiModel.todayLesson)

                binding.btnTodayLessonPlus.setOnSingleClickListener {
                    val isOutOfRange =
                        uiModel.todayLesson.presentNumber >= uiModel.todayLesson.totalNumber
                    if (isOutOfRange) return@setOnSingleClickListener

                    clickListener.onPlusClick(uiModel.todayLesson)
                    uiModel.todayLesson.presentNumber++
                    //TODO API 가 성공적으로 호출되서 결과를 받아올때 호출하고 싶음, 근데 애니메이션 같은 경우엔 뷰에서 작업을 해야할거고
                    updateDoingLessonProgress(holder, uiModel.todayLesson)
                }
                binding.btnTodayLessonMinus.setOnSingleClickListener {
                    val isOutOfRange = uiModel.todayLesson.presentNumber == 0
                    if (isOutOfRange) return@setOnSingleClickListener

                    clickListener.onMinusClick(uiModel.todayLesson)
                    uiModel.todayLesson.presentNumber--
                    updateDoingLessonProgress(holder, uiModel.todayLesson)
                }
            }
            is LessonUiModel.FinishedLesson -> (holder as TodayFinishedLessonViewHolder).apply {
                bind(uiModel.todayLesson)
                binding.btnTodayLessonPlus.setOnSingleClickListener {
                    val isOutOfRange =
                        uiModel.todayLesson.presentNumber >= uiModel.todayLesson.totalNumber
                    if (isOutOfRange) return@setOnSingleClickListener

                    clickListener.onPlusClick(uiModel.todayLesson)
                    uiModel.todayLesson.presentNumber++
                    updateFinishedLessonProgress(holder, uiModel.todayLesson)
                }
                binding.btnTodayLessonMinus.setOnSingleClickListener {
                    val isOutOfRange = uiModel.todayLesson.presentNumber == 0
                    if (isOutOfRange) return@setOnSingleClickListener

                    clickListener.onMinusClick(uiModel.todayLesson)
                    uiModel.todayLesson.presentNumber--
                    updateFinishedLessonProgress(holder, uiModel.todayLesson)
                }
                binding.clTodayFinishedBottom.setOnSingleClickListener {
                    clickListener.onFinishClick(uiModel.todayLesson)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LessonUiModel.LessonHeader -> R.layout.item_lesson_header
            is LessonUiModel.EmptyLesson -> R.layout.item_today_lesson_empty
            is LessonUiModel.DoingLesson -> R.layout.item_today_lesson_doing
            is LessonUiModel.FinishedLesson -> R.layout.item_today_lesson_finished
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    private fun updateDoingLessonProgress(
        holder: TodayDoingLessonViewHolder,
        todayLesson: TodayLessonResponse,
    ) {
        holder.binding.apply {
            val animation = ObjectAnimator.ofInt(
                holder.binding.pbTodayLessonBar,
                "progress",
                holder.binding.pbTodayLessonBar.progress, todayLesson.presentNumber * 1000
            )
            animation.apply {
                duration = DELAY_TIME
            }.start()
            tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
        }
    }

    private fun updateFinishedLessonProgress(
        holder: TodayFinishedLessonViewHolder,
        todayLesson: TodayLessonResponse,
    ) {
        holder.binding.apply {
            tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
        }
    }

    companion object {
        const val DELAY_TIME = 350L
    }
}

