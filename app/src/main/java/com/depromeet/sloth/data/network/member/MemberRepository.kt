package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.RetrofitServiceGeneratorTest
import javax.inject.Inject

class MemberRepository @Inject constructor(
    private val retrofitServiceGeneratorTest: RetrofitServiceGeneratorTest,
    private val preferenceManager: PreferenceManager
) {
    suspend fun fetchMemberInfo(): MemberState<MemberInfoResponse> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .fetchMemberInfo()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        MemberState.Success(this.body() ?: MemberInfoResponse())
                    }
                    else -> MemberState.Error(Exception(message()))
                }
            } ?: return MemberState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateMemberInfo(
        memberUpdateInfoRequest: MemberUpdateInfoRequest
    ): MemberUpdateState<MemberUpdateInfoResponse> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .updateMemberInfo(memberUpdateInfoRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        MemberUpdateState.Success(this.body() ?: MemberUpdateInfoResponse())
                    }
                    else -> MemberUpdateState.Error(Exception(message()))
                }
            } ?: return MemberUpdateState.Error(Exception("Retrofit Exception"))
    }

    suspend fun logout(): MemberLogoutState<String> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .logout()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        MemberLogoutState.Success(this.body() ?: "")
                    }
                    else -> MemberLogoutState.Error(Exception(message()))
                }
            } ?: return MemberLogoutState.Error(Exception("Retrofit Exception"))
    }

    fun removeAuthToken() {
        preferenceManager.removeAuthToken()
    }

    fun putFCMToken(fcmToken: String) {
        preferenceManager.putFCMToken(fcmToken)
    }
}