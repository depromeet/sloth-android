package com.depromeet.sloth.data.network.member

import java.lang.Exception

sealed class MemberLogoutState<out R> {
    data class Success<out T>(val data: T): MemberLogoutState<T>()
    object Created: MemberLogoutState<Nothing>()
    data class Unauthorized(val exception: Exception) : MemberLogoutState<Nothing>()
    data class Error(val exception: Exception): MemberLogoutState<Nothing>()
    object Forbidden : MemberLogoutState<Nothing>()
    object NotFound : MemberLogoutState<Nothing>()
}