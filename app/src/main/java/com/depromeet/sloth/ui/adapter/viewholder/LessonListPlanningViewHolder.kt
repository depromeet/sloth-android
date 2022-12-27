package com.depromeet.sloth.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.databinding.ItemHomeLessonListPlanningBinding
import com.depromeet.sloth.extensions.changeDecimalFormat

class LessonListPlanningViewHolder(
    private val binding: ItemHomeLessonListPlanningBinding,
    val onClick: (LessonAllResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lessonInfo: LessonAllResponse) {
        itemView.apply {
            binding.apply {
                tvLessonListCategory.text = lessonInfo.categoryName
                tvLessonListSite.text = lessonInfo.siteName
                tvLessonListName.text = lessonInfo.lessonName
                tvLessonListPrice.text = changeDecimalFormat(lessonInfo.price)
                tvLessonListPlanningDate.text = lessonInfo.startDate.replace("-", ".")

                clLessonList.setOnClickListener { onClick(lessonInfo) }
            }
        }
    }
}