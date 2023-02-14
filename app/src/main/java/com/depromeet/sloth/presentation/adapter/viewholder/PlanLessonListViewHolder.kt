package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.databinding.ItemLessonListPlanBinding
import com.depromeet.sloth.extensions.changeDecimalFormat

class PlanLessonListViewHolder(
    private val binding: ItemLessonListPlanBinding,
    val onClick: (LessonListResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lesson: LessonListResponse) {
        itemView.apply {
            binding.apply {
                tvLessonListCategory.text = lesson.categoryName
                tvLessonListSite.text = lesson.siteName
                tvLessonListName.text = lesson.lessonName
                tvLessonListPrice.text = changeDecimalFormat(lesson.price)
                tvLessonListPlanningDate.text = lesson.startDate.replace("-", ".")

                clLessonList.setOnClickListener { onClick(lesson) }
            }
        }
    }
}