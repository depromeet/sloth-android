package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.service.MemberService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
) : MemberRepository {

    override fun fetchMemberInfo() = flow {
        emit(Result.Loading)
        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build()
            .create(MemberService::class.java)
            .fetchMemberInfo(preferenceManager.getAccessToken()) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: MemberResponse.EMPTY))
            }
            401 -> {
                preferenceManager.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest
    ) = flow {
        emit(Result.Loading)
        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build()
            .create(MemberService::class.java)
            .updateMemberInfo(preferenceManager.getAccessToken(), memberUpdateRequest) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: MemberUpdateResponse.EMPTY))
            }
            401 -> {
                preferenceManager.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override suspend fun logout(): Result<String> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build()
            .create(MemberService::class.java)
            .logout(preferenceManager.getAccessToken())?.run {
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