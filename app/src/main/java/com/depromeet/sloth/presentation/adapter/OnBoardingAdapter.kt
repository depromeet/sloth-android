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
import com.depromeet.sloth.presentation.adapter.viewholder.TodayFinishedLessonViewHolder


class OnBoardingAdapter(
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
            DOING -> {
                TodayDoingLessonViewHolder (
                    parent.context,
                    ItemTodayLessonDoingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                TodayFinishedLessonViewHolder(
                    ItemTodayLessonFinishedBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DOING) {
            (holder as TodayDoingLessonViewHolder).bind(currentList[position])
        } else {
            (holder as TodayFinishedLessonViewHolder).bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        val isDoing = !currentList[position].untilTodayFinished
        val isFinished = currentList[position].untilTodayFinished

        return when {
            // 오늘 들어야 할 강의를 모든 듣지 않은 경우
            isDoing -> DOING
            // 오늘 들어야 할 강의를 모두 들은 경우
            else -> FINISHED
        }
    }

    companion object {
        private const val DOING = 2
        private const val FINISHED = 3
    }
}

