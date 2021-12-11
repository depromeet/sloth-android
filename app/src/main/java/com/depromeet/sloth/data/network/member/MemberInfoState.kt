package com.depromeet.sloth.data.network.member

import java.lang.Exception

sealed class MemberInfoState<out R>{
    data class Success<out T>(val data: T): MemberInfoState<T>()
    data class Error(val exception: Exception): MemberInfoState<Nothing>()
    object Unauthorized : MemberInfoState<Nothing>()
    object Forbidden : MemberInfoState<Nothing>()
    object NotFound : MemberInfoState<Nothing>()
}