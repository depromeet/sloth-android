package com.depromeet.sloth.data.network.member

import java.lang.Exception

sealed class MemberUpdateState<out R> {
    object Loading: MemberUpdateState<Nothing>()
    data class Success<out T>(val data: T): MemberUpdateState<T>()
    data class Unauthorized(val exception: Exception) : MemberUpdateState<Nothing>()
    data class Error(val exception: Exception): MemberUpdateState<Nothing>()
}

