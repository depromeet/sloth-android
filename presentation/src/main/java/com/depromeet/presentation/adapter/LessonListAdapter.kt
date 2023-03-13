package com.depromeet.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.adapter.viewholder.lessonlist.*
import com.depromeet.presentation.databinding.*
import com.depromeet.presentation.ui.lessonlist.LessonListItemClickListener
import com.depromeet.presentation.ui.lessonlist.LessonListUiModel
import com.depromeet.presentation.util.setOnSingleClickListener


class LessonListAdapter(
    private val clickListener: LessonListItemClickListener
) : ListAdapter<LessonListUiModel, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<LessonListUiModel>() {
        override fun areItemsTheSame(oldItem: LessonListUiModel, newItem: LessonListUiModel): Boolean {
            return (oldItem is LessonListUiModel.LessonListEmptyItem && newItem is LessonListUiModel.LessonListEmptyItem) ||
                    (oldItem is LessonListUiModel.LessonListCurrentItem && newItem is LessonListUiModel.LessonListCurrentItem && oldItem.lessonList.lessonId == newItem.lessonList.lessonId) ||
                    (oldItem is LessonListUiModel.LessonListPastItem && newItem is LessonListUiModel.LessonListPastItem && oldItem.lessonList.lessonId == newItem.lessonList.lessonId) ||
                    (oldItem is LessonListUiModel.LessonListPlanItem && newItem is LessonListUiModel.LessonListPlanItem && oldItem.lessonList.lessonId == newItem.lessonList.lessonId)
        }

        override fun areContentsTheSame(oldItem: LessonListUiModel, newItem: LessonListUiModel) =
            oldItem == newItem
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_lesson_list_title -> LessonListTitleViewHolder(
                ItemLessonListTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            R.layout.item_lesson_list_empty -> EmptyLessonViewHolder(
                ItemLessonListEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            R.layout.item_lesson_list_current -> CurrentLessonViewHolder(
                ItemLessonListCurrentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            R.layout.item_lesson_list_plan -> PlanLessonViewHolder(
                ItemLessonListPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            R.layout.item_lesson_list_past -> PastLessonViewHolder(
                ItemLessonListPastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val uiModel = getItem(position)) {
            is LessonListUiModel.LessonListEmptyItem -> (holder as EmptyLessonViewHolder).apply {
                binding.btnLessonListRegister.setOnSingleClickListener { clickListener.onRegisterClick() }
            }
            is LessonListUiModel.LessonListTitleItem -> (holder as LessonListTitleViewHolder).apply {
                bind(uiModel.itemType, uiModel.count)
            }
            is LessonListUiModel.LessonListPlanItem -> (holder as PlanLessonViewHolder).apply {
                bind(uiModel.lessonList)
                binding.clLessonList.setOnSingleClickListener { clickListener.onLessonClick(uiModel.lessonList) }
            }
            is LessonListUiModel.LessonListCurrentItem -> (holder as CurrentLessonViewHolder).apply {
                bind(uiModel.lessonList)
                binding.clLessonList.setOnSingleClickListener { clickListener.onLessonClick(uiModel.lessonList) }
            }
            is LessonListUiModel.LessonListPastItem -> (holder as PastLessonViewHolder).apply {
                bind(uiModel.lessonList)
                binding.clLessonList.setOnSingleClickListener { clickListener.onLessonClick(uiModel.lessonList) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is LessonListUiModel.LessonListTitleItem -> R.layout.item_lesson_list_title
            is LessonListUiModel.LessonListEmptyItem -> R.layout.item_lesson_list_empty
            is LessonListUiModel.LessonListPlanItem -> R.layout.item_lesson_list_plan
            is LessonListUiModel.LessonListCurrentItem -> R.layout.item_lesson_list_current
            is LessonListUiModel.LessonListPastItem -> R.layout.item_lesson_list_past
            else -> throw IllegalStateException("Unknown viewType")
        }
    }
}