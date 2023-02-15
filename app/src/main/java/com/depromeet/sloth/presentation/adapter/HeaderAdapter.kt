package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.databinding.ItemLessonHeaderBinding
import com.depromeet.sloth.presentation.adapter.viewholder.LessonHeaderViewHolder

class HeaderAdapter(
    private val headerType: HeaderType,
    private val count: Int? = null,
) : RecyclerView.Adapter<LessonHeaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonHeaderViewHolder {
        val binding =
            ItemLessonHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonHeaderViewHolder, position: Int) {
        holder.bind(headerType, count)
    }

    override fun getItemCount(): Int {
        return HEADER_TITLE_COUNT
    }

    enum class HeaderType(val title: String) {
        EMPTY("아래 버튼을 눌러 강의를 등록해주세요"),
        FINISHED("오늘까지 완료한 강의"),
        DOING("오늘까지 들어야하는 강의"),
        CURRENT("진행중인 강의"),
        PLAN("예정된 강의"),
        PAST("지난 강의")
    }

    companion object {
        const val HEADER_TITLE_COUNT = 1
    }
}