package com.depromeet.domain.usecase.member

import com.depromeet.domain.repository.MemberRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LogOutUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() : Flow<Result<String>> {
        return memberRepository.logout()
    }
}