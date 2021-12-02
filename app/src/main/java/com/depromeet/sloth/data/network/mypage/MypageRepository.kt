package com.depromeet.sloth.data.network.mypage

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.member.MemberInfoResponse
import com.depromeet.sloth.data.network.member.MemberInfoState

class MypageRepository {
    suspend fun fetchMemberInfo(accessToken: String): MypageState<MypageResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(MypageService::class.java)
            .fetchMemberInfo()?.run {
                return when(this.code()) {
                    200 -> MypageState.Success(this.body() ?: MypageResponse())
                    401 -> MypageState.Unauthorized
                    403 -> MypageState.Forbidden
                    404 -> MypageState.NotFound
                    else -> MypageState.Error(Exception("Uncaught Exception"))
                }
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