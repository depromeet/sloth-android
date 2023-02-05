package com.depromeet.sloth.domain.usecase.member

import com.depromeet.sloth.domain.repository.MemberRepository
import javax.inject.Inject

class FetchOnBoardingStatusUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke() : Boolean {
        return memberRepository.fetchOnBoardingStatus()
    }
}