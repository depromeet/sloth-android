package com.depromeet.sloth.ui.home.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.login.LoginGoogleResponse
import com.depromeet.sloth.data.network.login.LoginState
import com.depromeet.sloth.data.network.mypage.MypageRepository
import com.depromeet.sloth.data.network.mypage.MypageResponse
import com.depromeet.sloth.data.network.mypage.MypageState
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MypageViewModel: BaseViewModel() {
    private val mypageRepository = MypageRepository()

    suspend fun fetchMemberInfo(
        accessToken: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): MypageState<MypageResponse> = viewModelScope.async(
        context = context,
        start = start
    ) {
        mypageRepository.fetchMemberInfo(
            accessToken = accessToken
        )
    }.await()

    suspend fun updateMemberInfo(
        accessToken: String,
        updateMemberName: String,
        context: CoroutineContext = Dispatchers.IO,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): MypageState<Int> = viewModelScope.async(
        context = context,
        start = start
    ) {
        mypageRepository.updateMemberInfo(
            accessToken = accessToken,
            updateMemberName = updateMemberName
        )
    }.await()
}