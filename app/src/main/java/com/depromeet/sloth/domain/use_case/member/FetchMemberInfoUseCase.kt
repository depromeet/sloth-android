package com.depromeet.sloth.domain.use_case.member

import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.domain.repository.MemberRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchMemberInfoUseCase @Inject constructor (
    private val memberRepository: MemberRepository
) {
    operator fun invoke(): Flow<Result<MemberResponse>> {
        return memberRepository.fetchMemberInfo()
    }
}