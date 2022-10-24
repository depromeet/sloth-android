package com.depromeet.sloth.data.network.notification

import java.lang.Exception

sealed class NotificationState<out R> {
    object Loading: NotificationState<Nothing>()
    data class Success<out T>(val data: T): NotificationState<T>()
    data class Unauthorized(val exception: Exception) : NotificationState<Nothing>()
    data class Error(val exception: Exception): NotificationState<Nothing>()
}
