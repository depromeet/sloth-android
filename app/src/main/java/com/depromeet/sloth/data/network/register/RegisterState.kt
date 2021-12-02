package com.depromeet.sloth.data.network.register

import com.depromeet.sloth.data.network.mypage.MypageState
import java.lang.Exception

sealed class RegisterState<out R> {

    data class Success<out T>(val data: T): RegisterState<T>()
    data class Error(val exception: Exception): RegisterState<Nothing>()
    object Unauthorized : RegisterState<Nothing>()
    object Forbidden : RegisterState<Nothing>()
    object NotFound : RegisterState<Nothing>()
}