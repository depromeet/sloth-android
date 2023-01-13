package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemHomeTodayLessonDoingBinding
import com.depromeet.sloth.databinding.ItemHomeTodayLessonEmptyBinding
import com.depromeet.sloth.databinding.ItemHomeTodayLessonFinishedBinding
import com.depromeet.sloth.presentation.adapter.viewholder.TodayLessonDoingViewHolder
import com.depromeet.sloth.presentation.adapter.viewholder.TodayLessonEmptyViewHolder
import com.depromeet.sloth.presentation.adapter.viewholder.TodayLessonFinishedViewHolder


class TodayLessonAdapter(
    private val bodyType: BodyType,
    val onClick: (ClickType, LessonTodayResponse, Long) -> Unit
) : ListAdapter<LessonTodayResponse, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (bodyType) {
            BodyType.EMPTY -> TodayLessonEmptyViewHolder(
                ItemHomeTodayLessonEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClick,
            )

            BodyType.FINISHED -> TodayLessonFinishedViewHolder(
                ItemHomeTodayLessonFinishedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                currentList,
                onClick,
            )

            BodyType.NOT_FINISHED -> TodayLessonDoingViewHolder(
                parent.context,
                ItemHomeTodayLessonDoingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                currentList,
                onClick,
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (bodyType) {
            BodyType.EMPTY -> (holder as TodayLessonEmptyViewHolder).bind(currentList[position])
            BodyType.FINISHED -> (holder as TodayLessonFinishedViewHolder).bind(currentList[position])
            BodyType.NOT_FINISHED -> (holder as TodayLessonDoingViewHolder).bind(currentList[position])
        }
    }

    enum class BodyType {
        EMPTY,
        FINISHED,
        NOT_FINISHED
    }

    enum class ClickType(val value: Int) {
        CLICK_PLUS(1),
        CLICK_MINUS(-1),
        CLICK_NORMAL(0),
        CLICK_COMPLETE(2)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<LessonTodayResponse>() {
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
                return oldItem == newItem
            }
        }
    }
}

