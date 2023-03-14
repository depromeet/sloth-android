package com.depromeet.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.depromeet.presentation.adapter.viewholder.notification.NotificationViewHolder
import com.depromeet.presentation.databinding.ItemNotificationBinding
import com.depromeet.presentation.model.Notification
import com.depromeet.presentation.ui.notification.NotificationItemClickListener
import com.depromeet.presentation.util.setOnSingleClickListener


class NotificationPagingAdapter(private val clickListener: NotificationItemClickListener) : PagingDataAdapter<Notification, NotificationViewHolder>(
    object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.alarmId == newItem.alarmId
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
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