package com.depromeet.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.presentation.R
import com.depromeet.presentation.adapter.viewholder.notification.NotificationViewHolder
import com.depromeet.presentation.adapter.viewholder.notification.RestartOnBoardingViewHolder
import com.depromeet.presentation.databinding.ItemNotificationBinding
import com.depromeet.presentation.databinding.ItemRestartOnBoardingBinding
import com.depromeet.presentation.model.NotificationItem
import com.depromeet.presentation.ui.notification.NotificationItemClickListener
import com.depromeet.presentation.util.setOnSingleClickListener


class NotificationAdapter(private val clickListener: NotificationItemClickListener) :
    ListAdapter<NotificationItem, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<NotificationItem>() {
            override fun areItemsTheSame(
                oldItem: NotificationItem,
                newItem: NotificationItem
            ): Boolean {
                return when {
                    oldItem is NotificationItem.RestartOnBoarding && newItem is NotificationItem.RestartOnBoarding -> true
                    oldItem is NotificationItem.Notification && newItem is NotificationItem.Notification -> oldItem.alarmId == newItem.alarmId
                    else -> false
                }
            }

            override fun areContentsTheSame(
                oldItem: NotificationItem,
                newItem: NotificationItem
            ): Boolean {
                return when {
                    oldItem is NotificationItem.RestartOnBoarding && newItem is NotificationItem.RestartOnBoarding -> true
                    oldItem is NotificationItem.Notification && newItem is NotificationItem.Notification -> oldItem == newItem
                    else -> false
                }
            }
        }
    ) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NotificationItem.Notification -> R.layout.item_notification
            is NotificationItem.RestartOnBoarding -> R.layout.item_restart_on_boarding
            else -> throw IllegalStateException("Unknown viewType")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_notification -> NotificationViewHolder(
                ItemNotificationBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            R.layout.item_restart_on_boarding -> RestartOnBoardingViewHolder(
                ItemRestartOnBoardingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is NotificationItem.Notification -> {
                (holder as NotificationViewHolder).apply {
                    bind(item)
                    binding.root.setOnSingleClickListener {
                        clickListener.onNotificationClick(item)
                    }
                }
            }

            is NotificationItem.RestartOnBoarding -> {
                (holder as RestartOnBoardingViewHolder).apply {
                    binding.root.setOnSingleClickListener {
                        clickListener.onRestartOnBoardingClick()
                    }
                }
            }
        }
    }
}