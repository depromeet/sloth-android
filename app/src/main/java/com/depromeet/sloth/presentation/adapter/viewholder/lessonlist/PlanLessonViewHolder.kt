package com.depromeet.sloth.presentation.adapter.viewholder.lessonlist

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.ItemLessonListPlanBinding
import com.depromeet.sloth.extensions.changeDecimalFormat

class PlanLessonViewHolder(val binding: ItemLessonListPlanBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(lesson: LessonListResponse) {
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