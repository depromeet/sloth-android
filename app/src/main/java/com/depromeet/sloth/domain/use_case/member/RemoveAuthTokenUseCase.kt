package com.depromeet.sloth.domain.use_case.member

import com.depromeet.sloth.domain.repository.MemberRepository
import javax.inject.Inject

class RemoveAuthTokenUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() {
        memberRepository.removeAuthToken()
    }
}