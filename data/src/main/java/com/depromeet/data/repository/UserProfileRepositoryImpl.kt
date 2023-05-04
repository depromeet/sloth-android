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

    override fun updateUserProfile(userProfileUpdateRequestEntity: UserProfileUpdateRequestEntity, profileImageUrl: String?): Flow<Result<UserProfileUpdateEntity>>{
        return userProfileRemoteDataSource.updateUserProfile(userProfileUpdateRequestEntity, profileImageUrl)
    }

    override suspend fun checkTodayLessonOnBoardingStatus(): Boolean {
        return userProfileRemoteDataSource.checkTodayLessonOnBoardingStatus()
    }

    override suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean) {
        return userProfileRemoteDataSource.updateTodayLessonOnBoardingStatus(flag)
    }

    override suspend fun checkLessonListOnBoardingStatus(): Boolean {
        return userProfileRemoteDataSource.checkLessonListOnBoardingStatus()
    }

    override suspend fun updateLessonListOnBoardingStatus(flag: Boolean) {
        return userProfileRemoteDataSource.updateLessonListOnBoardingStatus(flag)
    }
}