package com.depromeet.presentation.ui.notification

import com.depromeet.presentation.model.Notification


data class NotificationItemClickListener(
    val onClick: (Notification) -> Unit,
)
