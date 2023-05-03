package com.depromeet.domain.repository

import com.depromeet.domain.entity.UserInfoEntity
import com.depromeet.domain.entity.UserProfileUpdateEntity
import com.depromeet.domain.entity.UserProfileUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface UserProfileRepository {

     fun fetchUserProfile(): Flow<Result<UserInfoEntity>>

    fun updateUserProfile(userProfileUpdateRequestEntity: UserProfileUpdateRequestEntity, profileImageUrl: String?): Flow<Result<UserProfileUpdateEntity>>

    suspend fun checkTodayLessonOnBoardingStatus(): Boolean

    suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean)

    suspend fun checkLessonListOnBoardingStatus(): Boolean

    suspend fun updateLessonListOnBoardingStatus(flag: Boolean)
}