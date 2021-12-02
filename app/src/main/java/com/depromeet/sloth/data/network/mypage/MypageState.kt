package com.depromeet.sloth.data.network.mypage

import com.depromeet.sloth.data.network.member.MemberInfoState
import java.lang.Exception


sealed class MypageState<out R> {
    data class Success<out T>(val data: T): MypageState<T>()
    data class Error(val exception: Exception): MypageState<Nothing>()
    object Unauthorized : MypageState<Nothing>()
    object Forbidden : MypageState<Nothing>()
    object NotFound : MypageState<Nothing>()
}

