package com.depromeet.domain.usecase.member

import com.depromeet.domain.repository.MemberRepository

import javax.inject.Inject


class DeleteAuthTokenUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke() {
        memberRepository.deleteAuthToken()
    }
}