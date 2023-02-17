package com.depromeet.sloth.domain.usecase.member

import com.depromeet.sloth.domain.repository.MemberRepository
import javax.inject.Inject

class FetchTodayLessonOnBoardingStatusUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke() : Boolean {
        return memberRepository.fetchTodayLessonOnBoardingStatus()
    }
}