package com.depromeet.presentation.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.adapter.viewholder.todaylesson.*
import com.depromeet.presentation.databinding.*
import com.depromeet.presentation.model.TodayLesson
import com.depromeet.presentation.ui.todaylesson.TodayLessonItemClickListener
import com.depromeet.presentation.ui.todaylesson.TodayLessonUiModel
import com.depromeet.presentation.util.setOnSingleClickListener


class TodayLessonAdapter(
    private val clickListener: TodayLessonItemClickListener
) : ListAdapter<TodayLessonUiModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<TodayLessonUiModel>() {
        override fun areItemsTheSame(oldItem: TodayLessonUiModel, newItem: TodayLessonUiModel): Boolean {
            return (oldItem is TodayLessonUiModel.TodayLessonEmptyItem && newItem is TodayLessonUiModel.TodayLessonEmptyItem) ||
                    (oldItem is TodayLessonUiModel.TodayLessonDoingItem && newItem is TodayLessonUiModel.TodayLessonDoingItem && oldItem.todayLesson.lessonId == newItem.todayLesson.lessonId) ||
                    (oldItem is TodayLessonUiModel.TodayLessonFinishedItem && newItem is TodayLessonUiModel.TodayLessonFinishedItem && oldItem.todayLesson.lessonId == newItem.todayLesson.lessonId)

        }

        override fun areContentsTheSame(oldItem: TodayLessonUiModel, newItem: TodayLessonUiModel): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_today_lesson_header -> TodayLessonHeaderViewHolder(
                ItemTodayLessonHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_today_lesson_title -> TodayLessonTitleViewHolder(
                ItemTodayLessonTitleBinding.inflate(
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
            is TodayLessonUiModel.TodayLessonHeaderItem -> (holder as TodayLessonHeaderViewHolder).apply {
                bind(uiModel.itemType)
            }
            is TodayLessonUiModel.TodayLessonTitleItem -> (holder as TodayLessonTitleViewHolder).apply {
                bind(uiModel.itemType)
            }
            is TodayLessonUiModel.TodayLessonEmptyItem -> (holder as TodayEmptyLessonViewHolder).apply {
                binding.clTodayLesson.setOnSingleClickListener { clickListener.onClick() }
            }
            is TodayLessonUiModel.TodayLessonDoingItem -> (holder as TodayDoingLessonViewHolder).apply {
                bind(uiModel.todayLesson)
                binding.apply {
                    btnTodayLessonPlus.setOnSingleClickListener {
                        viewTodayLessonLottie.playAnimation()
                        clickListener.onPlusClick(uiModel.todayLesson)
                        uiModel.todayLesson.presentNumber++
                        updateDoingLessonProgress(holder, uiModel.todayLesson)
                    }

                    btnTodayLessonMinus.setOnSingleClickListener {
                        val isOutOfRange = uiModel.todayLesson.presentNumber <= 0
                        if (isOutOfRange) return@setOnSingleClickListener

                        clickListener.onMinusClick(uiModel.todayLesson)
                        uiModel.todayLesson.presentNumber--
                        updateDoingLessonProgress(holder, uiModel.todayLesson)
                    }
                }
            }
            is TodayLessonUiModel.TodayLessonFinishedItem -> (holder as TodayFinishedLessonViewHolder).apply {
                bind(uiModel.todayLesson)
                binding.apply {
                    btnTodayLessonPlus.setOnSingleClickListener {
                        val isOutOfRange =
                            uiModel.todayLesson.presentNumber >= uiModel.todayLesson.totalNumber
                        if (isOutOfRange) return@setOnSingleClickListener

                        clickListener.onPlusClick(uiModel.todayLesson)
                        uiModel.todayLesson.presentNumber++
                        updateFinishedLessonProgress(holder, uiModel.todayLesson)
                    }

                    btnTodayLessonMinus.setOnSingleClickListener {
                        clickListener.onMinusClick(uiModel.todayLesson)
                        uiModel.todayLesson.presentNumber--
                        updateFinishedLessonProgress(holder, uiModel.todayLesson)
                    }

                    clTodayFinishedBottom.setOnSingleClickListener {
                        clickListener.onFinishClick(uiModel.todayLesson)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TodayLessonUiModel.TodayLessonHeaderItem -> R.layout.item_today_lesson_header
            is TodayLessonUiModel.TodayLessonTitleItem -> R.layout.item_today_lesson_title
            is TodayLessonUiModel.TodayLessonEmptyItem -> R.layout.item_today_lesson_empty
            is TodayLessonUiModel.TodayLessonDoingItem -> R.layout.item_today_lesson_doing
            is TodayLessonUiModel.TodayLessonFinishedItem -> R.layout.item_today_lesson_finished
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    private fun updateDoingLessonProgress(
        holder: TodayDoingLessonViewHolder,
        todayLesson: TodayLesson,
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

    private fun updateFinishedLessonProgress(holder: TodayFinishedLessonViewHolder, todayLesson: TodayLesson) {
        holder.binding.apply {
            tvTodayLessonCurrentNumber.text = todayLesson.presentNumber.toString()
        }
    }

    companion object {
        const val DELAY_TIME = 350L
    }
}