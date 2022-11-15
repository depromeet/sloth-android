package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.service.MemberService
import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val preferences: Preferences,
    private val service: MemberService
) : MemberRepository {

    override fun fetchMemberInfo() = flow {
        emit(Result.Loading)
        val response = service.fetchMemberInfo(preferences.getAccessToken()) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: MemberResponse.EMPTY))
            }
            401 -> {
                preferences.removeAuthToken()
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
        val response =
            service.updateMemberInfo(preferences.getAccessToken(), memberUpdateRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: MemberUpdateResponse.EMPTY))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun logout() = flow {
        emit(Result.Loading)
        val response = service.logout(preferences.getAccessToken()) ?: run {
            emit(Result.Error(Exception("Response is nukk")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: DEFAULT_STRING_VALUE))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> Result.Error(Exception(response.message()))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }


    override fun removeAuthToken() {
        preferences.removeAuthToken()
    }
}