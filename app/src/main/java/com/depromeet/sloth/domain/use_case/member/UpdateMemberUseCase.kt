package com.depromeet.sloth.domain.use_case.member

import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.domain.repository.MemberRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(memberUpdateRequest: MemberUpdateRequest): Flow<Result<MemberUpdateResponse>> {
        return memberRepository.updateMemberInfo(memberUpdateRequest)
    }
}