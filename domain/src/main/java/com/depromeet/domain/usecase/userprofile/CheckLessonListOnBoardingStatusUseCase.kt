package com.depromeet.domain.usecase.userprofile

import com.depromeet.domain.repository.UserProfileRepository
import javax.inject.Inject


class CheckLessonListOnBoardingStatusUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() : Boolean {
        return userProfileRepository.checkLessonListOnBoardingStatus()
    }
}