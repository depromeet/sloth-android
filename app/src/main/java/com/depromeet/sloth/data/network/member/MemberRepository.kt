package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import javax.inject.Inject

class MemberRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    suspend fun fetchMemberInfo(): MemberState<MemberInfoResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
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
        memberUpdateInfoRequest: MemberUpdateInfoRequest
    ): MemberUpdateState<MemberUpdateInfoResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .updateMemberInfo(memberUpdateInfoRequest)?.run {
                return when (this.code()) {
                    200 -> MemberUpdateState.Success(this.body() ?: MemberUpdateInfoResponse())
                    404 -> MemberUpdateState.NoContent
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(MemberService::class.java)
                            .updateMemberInfo(memberUpdateInfoRequest)?.run {
                                when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        MemberUpdateState.Success(body() ?: MemberUpdateInfoResponse())
                                    }
                                    else -> MemberUpdateState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: MemberUpdateState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> MemberUpdateState.Forbidden
                    else -> MemberUpdateState.Error(java.lang.Exception("Uncaught Exception"))
                }
            } ?: return MemberUpdateState.Error(java.lang.Exception("Register Exception"))
    }

    suspend fun logout(): MemberLogoutState<String> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .logout()?.run {
                return when (this.code()) {
                    200 -> MemberLogoutState.Success(this.body() ?: "")
                    201 -> MemberLogoutState.Created
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(MemberService::class.java)
                            .logout()?.run {
                                when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        MemberLogoutState.Success(body() ?: "")
                                    }
                                    else -> MemberLogoutState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: MemberLogoutState.Error(Exception("Uncaught Exception"))
                        }
                    403 -> MemberLogoutState.Forbidden
                    404 -> MemberLogoutState.NotFound
                    else -> MemberLogoutState.Error(java.lang.Exception("Uncaught Exception"))
                }
            } ?: return MemberLogoutState.Error(java.lang.Exception("Register Exception"))
    }

    fun removeAuthToken() {
        preferenceManager.removeAuthToken()
    }

    fun putFCMToken(fcmToken: String) {
        preferenceManager.putFCMToken(fcmToken)
    }
}