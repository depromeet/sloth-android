package com.depromeet.sloth.presentation.adapter.viewholder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ItemLessonHeaderBinding
import com.depromeet.sloth.presentation.adapter.HeaderAdapter

class LessonHeaderViewHolder(private val binding: ItemLessonHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(context: Context, headerType: HeaderAdapter.HeaderType, count: Int?) = with(binding) {
        tvHeaderTitle.text = headerType.title
        // 투데이 화면과 강의 목록 화면 구분
        if (count != null) {
            // 강의 목록 화면
            tvHeaderTitle.setTextColor(ContextCompat.getColor(context, R.color.gray_600))
            tvLessonCount.text =
                context.getString(R.string.doing_lesson_count, count.toString())
        }
    }
}