package com.depromeet.sloth.data.network.mypage

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class MypageRepository {
    suspend fun fetchMemberInfo(accessToken: String): MypageState<MypageResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(MypageService::class.java)
            .fetchMemberInfo()?.run {
                return MypageState.Success(
                    this.body() ?: MypageResponse()
                )
            } ?: return MypageState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateMemberInfo(
        accessToken: String,
        updateMemberName: String
    ): MypageState<Int> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(MypageService::class.java)
            .updateMemberInfo(
                MypageRequest(
                    memberName = updateMemberName
                )
            )?.run {
                return MypageState.Success(
                    0
                )
            } ?: return MypageState.Error(Exception("Register Exception"))
    }
}