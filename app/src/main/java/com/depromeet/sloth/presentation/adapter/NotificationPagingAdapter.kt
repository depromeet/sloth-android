package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.databinding.ItemNotificationBinding
import com.depromeet.sloth.presentation.adapter.viewholder.notification.NotificationViewHolder
import com.depromeet.sloth.presentation.ui.notification.NotificationItemClickListener
import com.depromeet.sloth.util.setOnSingleClickListener

class NotificationPagingAdapter(private val clickListener: NotificationItemClickListener) : PagingDataAdapter<NotificationListResponse, NotificationViewHolder>(
    object : DiffUtil.ItemCallback<NotificationListResponse>() {
        override fun areItemsTheSame(oldItem: NotificationListResponse, newItem: NotificationListResponse): Boolean {
            return oldItem.alarmId == newItem.alarmId
        }

        override fun areContentsTheSame(oldItem: NotificationListResponse, newItem: NotificationListResponse): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val pagedNotification = getItem(position)

        pagedNotification?.let { notification ->
            holder.bind(notification)
            holder.itemView.setOnSingleClickListener {
                clickListener.onClick(notification.alarmId)
            }
        }
    }
}