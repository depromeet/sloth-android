package com.depromeet.domain.usecase.member

import com.depromeet.domain.repository.MemberRepository
import javax.inject.Inject


class UpdateLessonListOnBoardingStatusUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(flag: Boolean) {
        memberRepository.updateLessonListOnBoardingStatus(flag)
    }
}