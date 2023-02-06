package com.depromeet.sloth.presentation.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.ItemLessonListPlanningBinding
import com.depromeet.sloth.extensions.changeDecimalFormat

class LessonListPlanningViewHolder(
    private val binding: ItemLessonListPlanningBinding,
    val onClick: (LessonAllResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lesson: LessonAllResponse) {
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