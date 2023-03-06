package com.depromeet.sloth.presentation.ui.notification

data class NotificationItemClickListener(
    val onClick: (Long) -> Unit,
)
