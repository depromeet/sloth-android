package com.depromeet.data.source.remote

import com.depromeet.domain.entity.UserInfoEntity
import com.depromeet.domain.entity.UserProfileUpdateEntity
import com.depromeet.domain.entity.UserProfileUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface UserProfileRemoteDataSource {

    fun fetchUserProfile(): Flow<Result<UserInfoEntity>>

    fun updateUserProfile(userProfileUpdateRequestEntity: UserProfileUpdateRequestEntity): Flow<Result<UserProfileUpdateEntity>>

    suspend fun fetchTodayLessonOnBoardingStatus(): Boolean

    suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean)

    suspend fun fetchLessonListOnBoardingStatus(): Boolean

    suspend fun updateLessonListOnBoardingStatus(flag: Boolean)

    suspend fun deleteAuthToken()
}