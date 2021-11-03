package com.depromeet.sloth.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R

class HeaderAdapter(
    private val headerType: HeaderType
) : RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_today_header, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.setHeaderText(headerType)
    }

    override fun getItemCount(): Int {
        return HEADER_TITLE_COUNT
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val todayItemTitle: TextView = itemView.findViewById(R.id.tv_today_item_title)

        fun setHeaderText(headerType: HeaderType) {
            todayItemTitle.text = headerType.title
        }
    }

    enum class HeaderType(val title: String) {
        FINISHED("이번주 완료한 강의"),
        NOT_FINISHED("이번주 들어야하는 강의")
    }

    companion object {
        const val HEADER_TITLE_COUNT = 1
    }
}