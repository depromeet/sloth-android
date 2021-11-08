package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.data.network.register.RegisterState
import java.lang.Exception

sealed class MemberInfoState<out R>{
    data class Success<out T>(val data: T): MemberInfoState<T>()
    data class Error(val exception: Exception): MemberInfoState<Nothing>()
}