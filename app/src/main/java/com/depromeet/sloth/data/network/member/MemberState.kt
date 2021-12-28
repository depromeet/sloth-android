package com.depromeet.sloth.data.network.member

import java.lang.Exception

sealed class MemberState<out R> {
    data class Success<out T>(val data: T): MemberState<T>()
    data class Error(val exception: Exception): MemberState<Nothing>()
    object Unauthorized : MemberState<Nothing>()
    object Forbidden : MemberState<Nothing>()
    object NotFound : MemberState<Nothing>()
}

