package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.RetrofitServiceGenerator

class MemberRepository(
    private val preferenceManager: PreferenceManager
) {
    suspend fun fetchMemberInfo(accessToken: String): MemberState<MemberInfoResponse> {
        RetrofitServiceGenerator.build(accessToken)
            .create(MemberService::class.java)
            .fetchMemberInfo()?.run {
                return when (this.code()) {
                    200 -> MemberState.Success(this.body() ?: MemberInfoResponse())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(MemberService::class.java)
                            .fetchMemberInfo()?.run {
                                when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        MemberState.Success(body() ?: MemberInfoResponse())
                                    }
                                    else -> MemberState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: MemberState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> MemberState.Forbidden
                    404 -> MemberState.NotFound
                    else -> MemberState.Error(Exception("Uncaught Exception"))
                }
            } ?: return MemberState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateMemberInfo(
        accessToken: String,
        memberUpdateInfoRequest: MemberUpdateInfoRequest
    ): MemberState<MemberUpdateInfoResponse> {
        RetrofitServiceGenerator.build(accessToken)
            .create(MemberService::class.java)
            .updateMemberInfo(memberUpdateInfoRequest)?.run {
                return when (this.code()) {
                    200 -> MemberState.Success(this.body() ?: MemberUpdateInfoResponse())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(MemberService::class.java)
                            .updateMemberInfo(memberUpdateInfoRequest)?.run {
                                when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        MemberState.Success(body() ?: MemberUpdateInfoResponse())
                                    }
                                    else -> MemberState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: MemberState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> MemberState.Forbidden
                    404 -> MemberState.NotFound
                    else -> MemberState.Error(java.lang.Exception("Uncaught Exception"))
                }
            } ?: return MemberState.Error(java.lang.Exception("Register Exception"))
    }
}