package com.depromeet.data.repository

import com.depromeet.data.source.remote.UserProfileRemoteDataSource
import com.depromeet.domain.entity.MemberEntity
import com.depromeet.domain.entity.MemberUpdateEntity
import com.depromeet.domain.entity.MemberUpdateRequestEntity
import com.depromeet.domain.repository.UserProfileRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileRemoteDataSource: UserProfileRemoteDataSource
) : UserProfileRepository {

    override fun fetchMemberInfo(): Flow<Result<MemberEntity>> {
        return userProfileRemoteDataSource.fetchMemberInfo()
    }

    override fun updateMemberInfo(memberUpdateRequestEntity: MemberUpdateRequestEntity): Flow<Result<MemberUpdateEntity>>{
        return userProfileRemoteDataSource.updateMemberInfo(memberUpdateRequestEntity)
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