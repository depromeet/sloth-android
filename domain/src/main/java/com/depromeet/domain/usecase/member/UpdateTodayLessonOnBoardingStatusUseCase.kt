package com.depromeet.domain.usecase.member

import com.depromeet.domain.repository.MemberRepository
import javax.inject.Inject


class UpdateTodayLessonOnBoardingStatusUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(flag: Boolean) {
        memberRepository.updateTodayLessonOnBoardingStatus(flag)
    }
}