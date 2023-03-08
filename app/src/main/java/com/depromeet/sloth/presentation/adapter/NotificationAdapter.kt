package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.databinding.ItemNotificationBinding
import com.depromeet.sloth.presentation.adapter.viewholder.notification.NotificationViewHolder
import com.depromeet.sloth.presentation.ui.notification.NotificationItemClickListener
import com.depromeet.sloth.util.setOnSingleClickListener

class NotificationAdapter(private val clickListener: NotificationItemClickListener) : ListAdapter<NotificationListResponse, NotificationViewHolder>(
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
        holder.apply {
            bind(currentList[position])
            binding.root.setOnSingleClickListener {
                clickListener.onClick(currentList[position].alarmId)
            }
        }
    }
}