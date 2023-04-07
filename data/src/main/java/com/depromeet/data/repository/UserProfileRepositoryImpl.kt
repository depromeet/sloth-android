package com.depromeet.data.repository

import com.depromeet.data.source.remote.UserProfileRemoteDataSource
import com.depromeet.domain.entity.UserInfoEntity
import com.depromeet.domain.entity.UserProfileUpdateEntity
import com.depromeet.domain.entity.UserProfileUpdateRequestEntity
import com.depromeet.domain.repository.UserProfileRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource
) : UserProfileRepository {

    override fun fetchUserProfile(): Flow<Result<UserInfoEntity>> {
        return userProfileRemoteDataSource.fetchUserProfile()
    }

    override fun updateUserProfile(userProfileUpdateRequestEntity: UserProfileUpdateRequestEntity): Flow<Result<UserProfileUpdateEntity>>{
        return userProfileRemoteDataSource.updateUserProfile(userProfileUpdateRequestEntity)
    }

    override suspend fun checkTodayLessonOnBoardingStatus(): Boolean {
        return userProfileRemoteDataSource.fetchTodayLessonOnBoardingStatus()
    }

    override suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean) {
        return userProfileRemoteDataSource.updateTodayLessonOnBoardingStatus(flag)
    }

    override suspend fun checkLessonListOnBoardingStatus(): Boolean {
        return userProfileRemoteDataSource.fetchLessonListOnBoardingStatus()
    }

    override suspend fun updateLessonListOnBoardingStatus(flag: Boolean) {
        return userProfileRemoteDataSource.updateLessonListOnBoardingStatus(flag)
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