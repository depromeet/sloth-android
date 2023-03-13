package com.depromeet.presentation.adapter.viewholder.lessonlist

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.databinding.ItemLessonListPlanBinding
import com.depromeet.presentation.extensions.changeDecimalFormat
import com.depromeet.presentation.model.LessonList


class PlanLessonViewHolder(val binding: ItemLessonListPlanBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(lesson: LessonList) {
        itemView.apply {
            binding.apply {
                tvLessonListCategory.text = lesson.categoryName
                tvLessonListSite.text = lesson.siteName
                tvLessonListName.text = lesson.lessonName
                tvLessonListPrice.text = changeDecimalFormat(lesson.price)
                tvLessonListPlanningDate.text = lesson.startDate.replace("-", ".")
            }
        }
    }
}