package com.depromeet.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.depromeet.presentation.adapter.viewholder.notification.NotificationViewHolder
import com.depromeet.presentation.databinding.ItemNotificationBinding
import com.depromeet.presentation.model.NotificationList
import com.depromeet.presentation.ui.notification.NotificationItemClickListener
import com.depromeet.presentation.util.setOnSingleClickListener


class NotificationAdapter(private val clickListener: NotificationItemClickListener) : ListAdapter<NotificationList, NotificationViewHolder>(
    object : DiffUtil.ItemCallback<NotificationList>() {
        override fun areItemsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
            return oldItem.alarmId == newItem.alarmId
        }

        override fun areContentsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
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