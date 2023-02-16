package com.depromeet.sloth.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.databinding.ItemNotificationBinding
import com.depromeet.sloth.presentation.adapter.viewholder.NotificationViewHolder

class NotificationAdapter(private val onNotificationClick: (Long) -> Unit) : RecyclerView.Adapter<NotificationViewHolder>() {

    private val notificationList = mutableListOf<NotificationListResponse>()

    fun setNotificationList(notificationList: List<NotificationListResponse>) {
        this.notificationList.clear()
        this.notificationList.addAll(notificationList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(holder.itemView.context, notificationList[position], onNotificationClick)
    }

    override fun getItemCount(): Int =
        this.notificationList.size
}