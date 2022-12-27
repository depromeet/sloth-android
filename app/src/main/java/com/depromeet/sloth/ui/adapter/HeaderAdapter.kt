package com.depromeet.sloth.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.databinding.ItemHomeHeaderBinding
import com.depromeet.sloth.ui.adapter.viewholder.HeaderViewHolder

class HeaderAdapter(
    private val headerType: HeaderType,
    private val count: Int? = null,
) : RecyclerView.Adapter<HeaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding =
            ItemHomeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(holder.itemView.context, headerType, count)
    }

    override fun getItemCount(): Int {
        return HEADER_TITLE_COUNT
    }

    enum class HeaderType(val title: String) {
        NOTHING("아래 버튼을 눌러 강의를 등록해주세요"),
        FINISHED("오늘까지 완료한 강의"),
        NOT_FINISHED("오늘까지 들어야하는 강의"),
        DOING("진행중인 강의"),
        PLANNING("예정된 강의"),
        PASSED("지난 강의")
    }

    companion object {
        const val HEADER_TITLE_COUNT = 1
    }
}