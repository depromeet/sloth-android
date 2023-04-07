package com.depromeet.data.source.remote

import com.depromeet.data.mapper.toEntity
import com.depromeet.data.mapper.toModel
import com.depromeet.data.model.response.member.MemberResponse
import com.depromeet.data.model.response.member.MemberUpdateResponse
import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.source.remote.service.UserProfileService
import com.depromeet.data.util.DEFAULT_STRING_VALUE
import com.depromeet.data.util.INTERNET_CONNECTION_ERROR
import com.depromeet.data.util.KEY_AUTHORIZATION
import com.depromeet.domain.entity.MemberUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class UserProfileRemoteDataSourceImpl @Inject constructor(
    private val userProfileService: UserProfileService,
    private val preferences: PreferenceManager,
) : UserProfileRemoteDataSource {

    override fun fetchMemberInfo() = flow {
        emit(Result.Loading)
        val response = userProfileService.fetchMemberInfo() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: MemberResponse.EMPTY.toEntity()))
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

    override fun updateMemberInfo(memberUpdateRequestEntity: MemberUpdateRequestEntity) = flow {
        emit(Result.Loading)
        val response =
            userProfileService.updateMemberInfo(memberUpdateRequestEntity.toModel())
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
                emit(Result.Success(response.body()?.toEntity() ?: MemberUpdateResponse.EMPTY.toEntity()))
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

    override suspend fun fetchTodayLessonOnBoardingStatus(): Boolean {
        return preferences.getTodayLessonOnBoardingStatus().first()
    }

    override suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean) {
        preferences.updateTodayLessonOnBoardingStatus(flag)
    }

    override suspend fun fetchLessonListOnBoardingStatus(): Boolean {
        return preferences.getLessonListOnBoardingStatus().first()
    }

    override suspend fun updateLessonListOnBoardingStatus(flag: Boolean) {
        preferences.updateLessonListOnBoardingStatus(flag)
    }

    override suspend fun deleteAuthToken() {
        preferences.deleteAuthToken()
    }

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
}