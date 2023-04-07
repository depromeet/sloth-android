package com.depromeet.domain.usecase.userprofile

import com.depromeet.domain.repository.UserProfileRepository
import javax.inject.Inject


class UpdateLessonListOnBoardingStatusUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(flag: Boolean) {
        userProfileRepository.updateLessonListOnBoardingStatus(flag)
    }
}