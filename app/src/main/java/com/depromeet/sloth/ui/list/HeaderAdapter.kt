package com.depromeet.sloth.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ItemHomeTodayHeaderBinding

class HeaderAdapter(
    private val headerType: HeaderType
) : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemHomeTodayHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.setHeaderText(headerType)
    }

    override fun getItemCount(): Int {
        return HEADER_TITLE_COUNT
    }

    inner class HeaderViewHolder(private val binding: ItemHomeTodayHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        private val todayItemTitle: TextView = itemView.findViewById(R.id.tv_today_item_title)

        fun setHeaderText(headerType: HeaderType) {
            todayItemTitle.text = headerType.title
        }
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