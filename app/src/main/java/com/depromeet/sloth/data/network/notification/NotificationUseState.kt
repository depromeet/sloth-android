package com.depromeet.sloth.data.network.notification

import java.lang.Exception

sealed class NotificationUseState<out R> {
    data class Success<out T>(val data: T): NotificationUseState<T>()
    data class Unauthorized(val exception: Exception) : NotificationUseState<Nothing>()
    data class Error(val exception: Exception): NotificationUseState<Nothing>()
    object NoContent: NotificationUseState<Nothing>()
    object Forbidden : NotificationUseState<Nothing>()
}
