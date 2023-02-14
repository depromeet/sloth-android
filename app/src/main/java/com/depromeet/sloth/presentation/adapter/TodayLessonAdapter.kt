package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemTodayLessonDoingBinding
import com.depromeet.sloth.databinding.ItemTodayLessonFinishedBinding
import com.depromeet.sloth.presentation.adapter.viewholder.TodayDoingLessonViewHolder
import com.depromeet.sloth.presentation.adapter.viewholder.TodayEmptyLessonViewHolder
import com.depromeet.sloth.presentation.adapter.viewholder.TodayFinishedLessonViewHolder


//TODO 끝난 강의에 대해선 두 가지의 클릭 이벤트(초과 수강, 강의 완강)가 존재 하므로 람다 함수를 두개 전달 해야함
class TodayLessonAdapter(
    val onClick: (LessonTodayResponse) -> Unit
) : ListAdapter<LessonTodayResponse, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<LessonTodayResponse>() {
        override fun areItemsTheSame(oldItem: LessonTodayResponse, newItem: LessonTodayResponse) =
            oldItem.lessonId == newItem.lessonId

        override fun areContentsTheSame(oldItem: LessonTodayResponse, newItem: LessonTodayResponse) =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EMPTY -> {
                TodayDoingLessonViewHolder (
                    parent.context,
                    ItemTodayLessonDoingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onClick
                )

            }

            DOING -> {
                TodayDoingLessonViewHolder (
                    parent.context,
                    ItemTodayLessonDoingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onClick
                )
            }

            else -> {
                TodayFinishedLessonViewHolder(
                    ItemTodayLessonFinishedBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), onClick
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            EMPTY -> {
                (holder as TodayEmptyLessonViewHolder).bind(currentList[position])
            }
            DOING -> {
                (holder as TodayDoingLessonViewHolder).bind(currentList[position])
            }
            else -> {
                (holder as TodayFinishedLessonViewHolder).bind(currentList[position])
            }
        }
    }

    // TODO EMPTY 판단 로직
    override fun getItemViewType(position: Int): Int {
        val isEmpty = currentList.isEmpty()
        val isDoing = !currentList[position].untilTodayFinished
        val isFinished = currentList[position].untilTodayFinished
        return when {
            isEmpty -> EMPTY
            isDoing -> DOING
            isFinished -> FINISHED
            else -> EMPTY
        }
    }

    companion object {
        private const val EMPTY = 1
        private const val DOING = 2
        private const val FINISHED = 3
    }
}

