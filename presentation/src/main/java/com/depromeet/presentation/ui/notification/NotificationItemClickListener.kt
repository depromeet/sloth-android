package com.depromeet.presentation.ui.notification


data class NotificationItemClickListener(
    val onClick: (Long) -> Unit,
)
