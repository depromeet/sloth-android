package com.depromeet.sloth.data.network.notification

import java.lang.Exception

sealed class NotificationSaveState<out R> {
    data class Success<out T>(val data: T): NotificationSaveState<T>()
    data class Unauthorized(val exception: Exception) : NotificationSaveState<Nothing>()
    data class Error(val exception: Exception): NotificationSaveState<Nothing>()
    object Created: NotificationSaveState<Nothing>()
    object Forbidden : NotificationSaveState<Nothing>()
    object NotFound : NotificationSaveState<Nothing>()
}
