package com.depromeet.domain.usecase.userprofile

import com.depromeet.domain.entity.UserProfileUpdateEntity
import com.depromeet.domain.entity.UserProfileUpdateRequestEntity
import com.depromeet.domain.repository.UserProfileRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    operator fun invoke(userProfileUpdateRequest: UserProfileUpdateRequestEntity, profileImageUrl: String?): Flow<Result<UserProfileUpdateEntity>> {
        return userProfileRepository.updateUserProfile(userProfileUpdateRequest, profileImageUrl)
    }
}