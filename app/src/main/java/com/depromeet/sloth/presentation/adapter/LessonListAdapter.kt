package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.*
import com.depromeet.sloth.presentation.adapter.viewholder.CurrentLessonListViewHolder
import com.depromeet.sloth.presentation.adapter.viewholder.EmptyLessonListViewHolder
import com.depromeet.sloth.presentation.adapter.viewholder.PastLessonListViewHolder
import com.depromeet.sloth.presentation.adapter.viewholder.PlanLessonListViewHolder

class LessonListAdapter(
    val onClick: (LessonListResponse) -> Unit
) : ListAdapter<LessonListResponse, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<LessonListResponse>() {
    override fun areItemsTheSame(oldItem: LessonListResponse, newItem: LessonListResponse) =
        oldItem.lessonId == newItem.lessonId

    override fun areContentsTheSame(oldItem: LessonListResponse, newItem: LessonListResponse) =
        oldItem == newItem
}) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            CURRENT -> {
                CurrentLessonListViewHolder(
                    ItemLessonListCurrentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClick
                )
            }
            PLANNING ->
                PlanLessonListViewHolder(
                    ItemLessonListPlanBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClick
                )
            else -> PastLessonListViewHolder(
                ItemLessonListPastBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                onClick
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            EMPTY -> (holder as EmptyLessonListViewHolder).bind(currentList[position])
            CURRENT -> (holder as CurrentLessonListViewHolder).bind(currentList[position])
            PLANNING -> (holder as PlanLessonListViewHolder).bind(currentList[position])
            PAST -> (holder as PastLessonListViewHolder).bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        // TODO isEmpty 판별 하는 방법
        val isEmpty = currentList.isEmpty()
        val isCurrentLesson =
            !currentList[position].isFinished && currentList[position].lessonStatus == "CURRENT"
        val isPlanLesson = currentList[position].lessonStatus == "PLAN"
        val isPast = currentList[position].isFinished || currentList[position].lessonStatus == "PAST"

        return when {
            isEmpty -> EMPTY
            isCurrentLesson -> CURRENT
            isPlanLesson -> PLANNING
            isPast -> PAST
            else -> EMPTY
        }
    }
    companion object {
        private const val EMPTY = 1
        private const val CURRENT = 2
        private const val PLANNING = 3
        private const val PAST = 4
    }
}