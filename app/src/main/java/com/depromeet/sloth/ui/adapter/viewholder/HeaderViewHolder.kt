package com.depromeet.sloth.ui.adapter.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ItemHomeHeaderBinding
import com.depromeet.sloth.ui.adapter.HeaderAdapter

class HeaderViewHolder(private val binding: ItemHomeHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(context: Context, headerType: HeaderAdapter.HeaderType, count: Int?) {
        binding.tvHeaderTitle.text = headerType.title
        if (count != null) {
            binding.tvDoingLessonCount.text =
                context.getString(R.string.doing_lesson_count, count.toString())
        }
    }
}