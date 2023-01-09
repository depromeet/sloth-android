package com.depromeet.sloth.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.ItemHomeLessonListDoingBinding
import com.depromeet.sloth.databinding.ItemHomeLessonListEmptyBinding
import com.depromeet.sloth.databinding.ItemHomeLessonListFinishedBinding
import com.depromeet.sloth.databinding.ItemHomeLessonListPlanningBinding
import com.depromeet.sloth.ui.adapter.viewholder.LessonListDoingViewHolder
import com.depromeet.sloth.ui.adapter.viewholder.LessonListEmptyViewHolder
import com.depromeet.sloth.ui.adapter.viewholder.LessonListPassedViewHolder
import com.depromeet.sloth.ui.adapter.viewholder.LessonListPlanningViewHolder

class LessonListAdapter(
    private val bodyType: BodyType,
    val onClick: (LessonAllResponse) -> Unit
) : ListAdapter<LessonAllResponse, RecyclerView.ViewHolder>(LessonListDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (bodyType) {
            BodyType.Empty -> LessonListEmptyViewHolder(
                ItemHomeLessonListEmptyBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false), onClick
            )
            BodyType.DOING -> LessonListDoingViewHolder(
                ItemHomeLessonListDoingBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false), onClick
            )
            BodyType.PLANNING -> LessonListPlanningViewHolder(
                ItemHomeLessonListPlanningBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false), onClick
            )
            BodyType.PASSED -> LessonListPassedViewHolder(
                ItemHomeLessonListFinishedBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false), onClick
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (bodyType) {
            BodyType.Empty -> (holder as LessonListEmptyViewHolder).bind(currentList[position])
            BodyType.DOING -> (holder as LessonListDoingViewHolder).bind(currentList[position])
            BodyType.PLANNING -> (holder as LessonListPlanningViewHolder).bind(currentList[position])
            BodyType.PASSED -> (holder as LessonListPassedViewHolder).bind(currentList[position])
        }
    }

    enum class BodyType {
        Empty,
        DOING,
        PLANNING,
        PASSED
    }
}

object LessonListDiffCallback : DiffUtil.ItemCallback<LessonAllResponse>() {
    override fun areItemsTheSame(
        oldItem: LessonAllResponse,
        newItem: LessonAllResponse,
    ): Boolean {
        return oldItem.lessonId == newItem.lessonId
    }

    override fun areContentsTheSame(
        oldItem: LessonAllResponse,
        newItem: LessonAllResponse,
    ): Boolean {
        return oldItem == newItem
    }
}