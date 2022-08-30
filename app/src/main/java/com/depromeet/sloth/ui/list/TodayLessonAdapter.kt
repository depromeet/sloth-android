package com.depromeet.sloth.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.databinding.ItemHomeTodayLessonDoingBinding
import com.depromeet.sloth.databinding.ItemHomeTodayLessonFinishedBinding
import com.depromeet.sloth.databinding.ItemHomeTodayLessonNothingBinding
import com.depromeet.sloth.ui.list.viewholder.TodayLessonDoingViewHolder
import com.depromeet.sloth.ui.list.viewholder.TodayLessonFinishedViewHolder
import com.depromeet.sloth.ui.list.viewholder.TodayLessonNothingViewHolder


class TodayLessonAdapter(
    private val bodyType: BodyType,
    val onClick: (ClickType, LessonTodayResponse) -> Unit
) : ListAdapter<LessonTodayResponse, RecyclerView.ViewHolder>(
    TodayLessonDiffCallback
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when(bodyType) {
            BodyType.NOTHING -> TodayLessonNothingViewHolder(
                ItemHomeTodayLessonNothingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClick,
            )
            BodyType.FINISHED -> TodayLessonFinishedViewHolder(
                ItemHomeTodayLessonFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                currentList,
                onClick,

            )
            BodyType.NOT_FINISHED -> TodayLessonDoingViewHolder(
                ItemHomeTodayLessonDoingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                currentList,
                onClick,
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (bodyType) {
            BodyType.NOTHING -> (holder as TodayLessonNothingViewHolder).bind(currentList[position])
            BodyType.FINISHED -> (holder as TodayLessonFinishedViewHolder).bind(currentList[position])
            BodyType.NOT_FINISHED -> (holder as TodayLessonDoingViewHolder).bind(currentList[position])
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
        return oldItem == newItem
    }
}