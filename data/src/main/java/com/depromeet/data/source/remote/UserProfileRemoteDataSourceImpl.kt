package com.depromeet.data.source.remote

import com.depromeet.data.mapper.toEntity
import com.depromeet.data.mapper.toModel
import com.depromeet.data.model.response.userprofile.UserProfileResponse
import com.depromeet.data.model.response.userprofile.UserProfileUpdateResponse
import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.source.remote.service.UserProfileService
import com.depromeet.data.util.RESPONSE_NULL_ERROR
import com.depromeet.data.util.handleExceptions
import com.depromeet.data.util.handleResponse
import com.depromeet.domain.entity.UserProfileUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserProfileRemoteDataSourceImpl @Inject constructor(
    private val userProfileService: UserProfileService,
    private val preferences: PreferenceManager,
) : UserProfileRemoteDataSource {

    override fun fetchUserProfile() = flow {
        emit(Result.Loading)
        val response = userProfileService.fetchUserProfile() ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: UserProfileResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override fun updateUserProfile(userProfileUpdateRequestEntity: UserProfileUpdateRequestEntity) = flow {
        emit(Result.Loading)
        val response = userProfileService.updateUserProfile(userProfileUpdateRequestEntity.toModel())
            ?: run {
                emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                return@flow
            }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: UserProfileUpdateResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override suspend fun checkTodayLessonOnBoardingStatus(): Boolean {
        return preferences.getTodayLessonOnBoardingStatus().first()
    }

    override suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean) {
        preferences.updateTodayLessonOnBoardingStatus(flag)
    }

    override suspend fun checkLessonListOnBoardingStatus(): Boolean {
        return preferences.getLessonListOnBoardingStatus().first()
    }

    override suspend fun updateLessonListOnBoardingStatus(flag: Boolean) {
        preferences.updateLessonListOnBoardingStatus(flag)
    }

    override suspend fun deleteAuthToken() {
        preferences.deleteAuthToken()
    }
}