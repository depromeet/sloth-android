package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.*
import com.depromeet.sloth.presentation.adapter.viewholder.*
import com.depromeet.sloth.presentation.screen.lessonlist.LessonUiModel
import com.depromeet.sloth.util.setOnSingleClickListener

class LessonListAdapter(
    val onClick: (LessonListResponse) -> Unit
) : ListAdapter<LessonUiModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<LessonUiModel>() {
        override fun areItemsTheSame(oldItem: LessonUiModel, newItem: LessonUiModel): Boolean {
            return (oldItem is LessonUiModel.EmptyLesson && newItem is LessonUiModel.EmptyLesson) ||
                    (oldItem is LessonUiModel.CurrentLesson && newItem is LessonUiModel.CurrentLesson && oldItem.lessonListResponse.lessonId == newItem.lessonListResponse.lessonId) ||
                    (oldItem is LessonUiModel.PastLesson && newItem is LessonUiModel.PastLesson && oldItem.lessonListResponse.lessonId == newItem.lessonListResponse.lessonId) ||
                    (oldItem is LessonUiModel.PlanLesson && newItem is LessonUiModel.PlanLesson && oldItem.lessonListResponse.lessonId == newItem.lessonListResponse.lessonId)
        }

        override fun areContentsTheSame(oldItem: LessonUiModel, newItem: LessonUiModel) =
            oldItem == newItem
    }) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_lesson_header -> LessonHeaderViewHolder(
                ItemLessonHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_lesson_list_empty -> EmptyLessonListViewHolder(
                ItemLessonListEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_lesson_list_current -> CurrentLessonListViewHolder(
                ItemLessonListCurrentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_lesson_list_plan -> PlanLessonListViewHolder(
                ItemLessonListPlanBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            R.layout.item_lesson_list_past -> PastLessonListViewHolder(
                ItemLessonListPastBinding.inflate(
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
                (holder as LessonHeaderViewHolder).bind(uiModel.headerType, uiModel.count)

            is LessonUiModel.EmptyLesson -> (holder as EmptyLessonListViewHolder).binding.apply {
                btnLessonListRegister.setOnSingleClickListener {
                    onClick(LessonListResponse.EMPTY)
                }
            }
            is LessonUiModel.PlanLesson -> (holder as PlanLessonListViewHolder).apply {
                bind(uiModel.lessonListResponse)
                binding.clLessonList.setOnSingleClickListener { onClick(uiModel.lessonListResponse) }
            }
            is LessonUiModel.CurrentLesson -> (holder as CurrentLessonListViewHolder).apply {
                bind(uiModel.lessonListResponse)
                binding.clLessonList.setOnSingleClickListener { onClick(uiModel.lessonListResponse) }
            }
            is LessonUiModel.PastLesson -> (holder as PastLessonListViewHolder).apply {
                bind(uiModel.lessonListResponse)
                binding.clLessonList.setOnSingleClickListener { onClick(uiModel.lessonListResponse) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LessonUiModel.LessonHeader -> R.layout.item_lesson_header
            is LessonUiModel.EmptyLesson -> R.layout.item_lesson_list_empty
            is LessonUiModel.PlanLesson -> R.layout.item_lesson_list_plan
            is LessonUiModel.CurrentLesson -> R.layout.item_lesson_list_current
            is LessonUiModel.PastLesson -> R.layout.item_lesson_list_past
            else -> throw IllegalStateException("Unknown viewType")
        }
    }
}