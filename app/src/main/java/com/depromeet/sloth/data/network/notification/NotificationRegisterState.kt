package com.depromeet.sloth.data.network.notification

import com.depromeet.sloth.data.network.member.MemberUpdateState
import java.lang.Exception

sealed class NotificationRegisterState<out R> {
    object Loading: NotificationRegisterState<Nothing>()
    data class Success<out T>(val data: T): NotificationRegisterState<T>()
    data class Unauthorized(val exception: Exception) : NotificationRegisterState<Nothing>()
    data class Error(val exception: Exception): NotificationRegisterState<Nothing>()
    object Created: NotificationRegisterState<Nothing>()
    object Forbidden : NotificationRegisterState<Nothing>()
    object NotFound : NotificationRegisterState<Nothing>()
}
