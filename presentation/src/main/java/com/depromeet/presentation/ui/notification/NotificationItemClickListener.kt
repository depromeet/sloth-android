package com.depromeet.presentation.ui.notification

import com.depromeet.presentation.model.NotificationItem
import kotlinx.coroutines.Job


data class NotificationItemClickListener(
    val onRestartOnBoardingClick: () -> Job,
    val onNotificationClick: (NotificationItem.Notification) -> Unit,
)
