package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator

class MemberRepository {
    suspend fun fetchMemberInfo(accessToken: String): MemberState<MemberInfoResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(MemberService::class.java)
            .fetchMemberInfo()?.run {
                return when(this.code()) {
                    200 -> MemberState.Success(this.body() ?: MemberInfoResponse())
                    401 -> MemberState.Unauthorized
                    403 -> MemberState.Forbidden
                    404 -> MemberState.NotFound
                    else -> MemberState.Error(Exception("Uncaught Exception"))
                }
            } ?: return MemberState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateMemberInfo(
        accessToken: String,
        updateMemberInfoRequest: UpdateMemberInfoRequest
    ): MemberState<UpdateMemberInfoResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(MemberService::class.java)
            .updateMemberInfo(
                UpdateMemberInfoRequest(
                    memberName = updateMemberInfoRequest.memberName
                )
            )?.run {
                return when(this.code()) {
                    200 -> MemberState.Success(this.body() ?: UpdateMemberInfoResponse() )
                    401 -> MemberState.Unauthorized
                    403 -> MemberState.Forbidden
                    404 -> MemberState.NotFound
                    else -> MemberState.Error(java.lang.Exception("Uncaught Exception"))
                }
            } ?: return MemberState.Error(java.lang.Exception("Register Exception"))
    }
}