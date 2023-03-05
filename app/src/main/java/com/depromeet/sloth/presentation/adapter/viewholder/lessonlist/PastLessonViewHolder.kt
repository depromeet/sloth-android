package com.depromeet.sloth.presentation.adapter.viewholder.lessonlist

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.ItemLessonListPastBinding
import com.depromeet.sloth.extensions.changeDecimalFormat
import kotlin.math.ceil

class PastLessonViewHolder(val binding: ItemLessonListPastBinding, ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(lesson: LessonListResponse) {
        itemView.apply {
            binding.apply {
                tvLessonListCategory.text = lesson.categoryName
                tvLessonListSite.text = lesson.siteName
                tvLessonListName.text = lesson.lessonName
                tvLessonListPrice.text = changeDecimalFormat(lesson.price)
                tvLessonListTotalCount.text = lesson.totalNumber.toString()

                val progressRate = (lesson.currentProgressRate / 100.0f)
                val isComplete =
                    (ceil(progressRate * lesson.totalNumber)).toInt() == lesson.totalNumber
                if (isComplete) {
                    ivLessonListStamp.setImageResource(R.drawable.ic_lesson_list_success)
                } else {
                    ivLessonListStamp.setImageResource(R.drawable.ic_lesson_list_fail)
                }
            }
        }
    }
}