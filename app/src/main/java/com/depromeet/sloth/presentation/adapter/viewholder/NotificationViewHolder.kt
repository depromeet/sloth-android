package com.depromeet.sloth.presentation.adapter.viewholder

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.databinding.ItemNotificationBinding

class NotificationViewHolder(private val binding: ItemNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        context: Context,
        item: NotificationListResponse,
        onNotificationClick: (Long) -> Unit
    ) = with(binding) {
        binding.clNotificationBackground.setOnClickListener {
            clNotificationBackground.setBackgroundColor(Color.WHITE)
            tvNotificationTitle.setTextColor(context.getColor(R.color.gray_600))
            tvNotificationContent.setTextColor(context.getColor(R.color.gray_500))
            tvNotificationDate.setTextColor(context.getColor(R.color.gray_400))

            onNotificationClick.invoke(item.alarmId)
        }

        if(item.readTime.isNullOrEmpty()) {
            clNotificationBackground.setBackgroundColor(context.getColor(R.color.gray_100))
            tvNotificationTitle.setTextColor(context.getColor(R.color.gray_400))
            tvNotificationContent.setTextColor(context.getColor(R.color.gray_400))
            tvNotificationDate.setTextColor(context.getColor(R.color.gray_400))
        } else {
            clNotificationBackground.setBackgroundColor(Color.WHITE)
            tvNotificationTitle.setTextColor(context.getColor(R.color.gray_600))
            tvNotificationContent.setTextColor(context.getColor(R.color.gray_500))
            tvNotificationDate.setTextColor(context.getColor(R.color.gray_400))
        }

        tvNotificationTitle.text = item.message
        tvNotificationContent.text = item.title
        tvNotificationDate.text = item.occurrenceTime.split(" ").first()
    }
}