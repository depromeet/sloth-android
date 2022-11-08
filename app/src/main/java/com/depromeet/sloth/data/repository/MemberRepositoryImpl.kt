package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.service.MemberService
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.common.Result
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
): MemberRepository {

    override suspend fun fetchMemberInfo(): Result<MemberResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .fetchMemberInfo()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: MemberResponse())
                    }
                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override suspend fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest,
    ): Result<MemberUpdateResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .updateMemberInfo(memberUpdateRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: MemberUpdateResponse())
                    }
                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override suspend fun logout(): Result<String> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .logout()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: "")
                    }
                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override fun removeAuthToken() {
        preferenceManager.removeAuthToken()
    }
}