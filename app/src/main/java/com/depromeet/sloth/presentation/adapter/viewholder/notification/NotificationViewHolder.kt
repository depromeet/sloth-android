package com.depromeet.sloth.presentation.adapter.viewholder.notification

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.databinding.ItemNotificationBinding

class NotificationViewHolder(val binding: ItemNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        notification: NotificationListResponse,
    ) = with(binding) {
        tvNotificationTitle.text = notification.title
        tvNotificationContent.text = notification.message
        tvNotificationDate.text = notification.occurrenceTime.split(" ").first()

        if(notification.readTime.isNullOrEmpty()) {
            clNotificationBackground.setBackgroundColor(Color.WHITE)
            tvNotificationTitle.setTextColor(itemView.context.getColor(R.color.gray_600))
            tvNotificationContent.setTextColor(itemView.context.getColor(R.color.gray_500))
            tvNotificationDate.setTextColor(itemView.context.getColor(R.color.gray_400))
        } else {
            clNotificationBackground.setBackgroundColor(itemView.context.getColor(R.color.gray_100))
            tvNotificationTitle.setTextColor(itemView.context.getColor(R.color.gray_400))
            tvNotificationContent.setTextColor(itemView.context.getColor(R.color.gray_400))
            tvNotificationDate.setTextColor(itemView.context.getColor(R.color.gray_400))
        }
    }
}