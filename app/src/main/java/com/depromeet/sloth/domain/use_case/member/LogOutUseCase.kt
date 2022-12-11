package com.depromeet.sloth.domain.use_case.member

import com.depromeet.sloth.domain.repository.MemberRepository
import com.depromeet.sloth.common.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke() : Flow<Result<String>> {
        return memberRepository.logout()
    }
}