package com.depromeet.domain.usecase.userprofile

import com.depromeet.domain.entity.UserInfoEntity
import com.depromeet.domain.repository.UserProfileRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchUserProfileUseCase @Inject constructor (
    private val userProfileRepository: UserProfileRepository
) {
    operator fun invoke(): Flow<Result<UserInfoEntity>> {
        return userProfileRepository.fetchUserProfile()
    }
}