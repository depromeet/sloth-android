package com.depromeet.sloth.data.network.member

import java.lang.Exception

sealed class MemberLogoutState<out R> {
    object Loading: MemberLogoutState<Nothing>()
    data class Success<out T>(val data: T): MemberLogoutState<T>()
    data class Unauthorized(val exception: Exception) : MemberLogoutState<Nothing>()
    data class Error(val exception: Exception): MemberLogoutState<Nothing>()
}