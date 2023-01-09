package com.depromeet.sloth.domain.use_case.member

import com.depromeet.sloth.domain.repository.MemberRepository
import javax.inject.Inject

class DeleteAuthTokenUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke() {
        memberRepository.deleteAuthToken()
    }
}