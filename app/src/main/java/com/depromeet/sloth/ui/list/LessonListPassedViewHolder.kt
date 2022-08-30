package com.depromeet.sloth.ui.list

import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.databinding.ItemHomeLessonListFinishedBinding
import com.depromeet.sloth.extensions.changeDecimalFormat
import kotlin.math.ceil

class LessonListPassedViewHolder(
    private val binding: ItemHomeLessonListFinishedBinding,
    val onClick: (LessonAllResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(lessonInfo: LessonAllResponse) {
        itemView.apply {
            binding.apply {
                tvLessonListCategory.text = lessonInfo.categoryName
                tvLessonListSite.text = lessonInfo.siteName
                tvLessonListName.text = lessonInfo.lessonName
                tvLessonListPrice.text = changeDecimalFormat(lessonInfo.price)
                tvLessonListTotalCount.text = lessonInfo.totalNumber.toString()
                val progressRate = (lessonInfo.currentProgressRate / 100.0f)
                val isComplete =
                    (ceil(progressRate * lessonInfo.totalNumber)).toInt() == lessonInfo.totalNumber
                if (isComplete) {
                    ivLessonListStamp.setImageResource(R.drawable.ic_home_lesson_list_success)
                } else {
                    ivLessonListStamp.setImageResource(R.drawable.ic_home_lesson_list_fail)
                }

                clLessonList.setOnClickListener { onClick(lessonInfo) }
            }
        }
    }
}