package com.depromeet.sloth.data.network.member

import java.lang.Exception

sealed class MemberState<out R> {
    object Loading: MemberState<Nothing>()
    data class Success<out T>(val data: T): MemberState<T>()
    data class Unauthorized(val exception: Exception) : MemberState<Nothing>()
    data class Error(val exception: Exception): MemberState<Nothing>()
    object Forbidden : MemberState<Nothing>()
    object NotFound : MemberState<Nothing>()
}

