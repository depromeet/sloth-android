package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.service.MemberService
import com.depromeet.sloth.data.preferences.PreferenceManager
import com.depromeet.sloth.domain.repository.MemberRepository
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val preferences: PreferenceManager,
    private val memberService: MemberService
) : MemberRepository {

//    suspend fun handleResponse(response: Response<BaseResponse>, preferences: PreferenceManager) {
//        when(response.code()) {
//            200 -> {
//                // 토큰 갱신
//                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
//                if (newAccessToken.isNotEmpty()) {
//                    preferences.updateAccessToken(newAccessToken)
//                }
//                emit(Result.Success(response.body() ?: response.EMPTY))
//            }
//            else -> {
//                emit(Result.Error(Exception(response.message()), response.code()))
//            }
//        }
//    }
//
//    suspend fun handleError(throwable: Throwable) {
//        when(throwable) {
//            is IOException -> {
//                // Handle Internet connection error
//                emit(Result.Error(Exception("Internet connect Error")))
//            }
//            else -> {
//                // Handle other error
//                emit(Result.Error(throwable))
//            }
//        }
//    }

    override fun fetchMemberInfo() = flow {
        emit(Result.Loading)
        val response = memberService.fetchMemberInfo() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: MemberResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest
    ) = flow {
        emit(Result.Loading)
        val response =
            memberService.updateMemberInfo(memberUpdateRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: MemberUpdateResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun logout() = flow {
        emit(Result.Loading)
        val response = memberService.logout() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: DEFAULT_STRING_VALUE))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override suspend fun removeAuthToken() {
        preferences.removeAuthToken()
    }
}