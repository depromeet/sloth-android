package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.ItemLessonListFinishedBinding
import com.depromeet.sloth.extensions.changeDecimalFormat
import kotlin.math.ceil

class LessonListPassedViewHolder(
    private val binding: ItemLessonListFinishedBinding,
    val onClick: (LessonAllResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lesson: LessonAllResponse) {
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

                clLessonList.setOnClickListener { onClick(lesson) }
            }
        }
    }
}