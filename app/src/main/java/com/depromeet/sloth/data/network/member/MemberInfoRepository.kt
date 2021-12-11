package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.detail.LessonDetailState
import com.depromeet.sloth.data.network.mypage.MypageRequest
import com.depromeet.sloth.data.network.mypage.MypageService
import com.depromeet.sloth.data.network.mypage.MypageState
import java.lang.Exception

class MemberInfoRepository {
    suspend fun fetchMemberInfo(
        accessToken: String
    ): MemberInfoState<MemberInfoResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(MemberInfoService::class.java)
            .fetchMemberInfo()?.run {
                return when(this.code()) {
                    200 -> MemberInfoState.Success(this.body() ?: MemberInfoResponse())
                    401 -> MemberInfoState.Unauthorized
                    403 -> MemberInfoState.Forbidden
                    404 -> MemberInfoState.NotFound
                    else -> MemberInfoState.Error(Exception("Uncaught Exception"))
                }
            } ?: return MemberInfoState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateMemberInfo(
        accessToken: String,
        updateMemberName: String
    ): MemberInfoState<Int> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(MemberInfoService::class.java)
            .updateMemberInfo(
                MypageRequest(
                    memberName = updateMemberName
                )
            )?.run {
                return MemberInfoState.Success(
                    0
                )
            } ?: return MemberInfoState.Error(Exception("Register Exception"))
    }
}