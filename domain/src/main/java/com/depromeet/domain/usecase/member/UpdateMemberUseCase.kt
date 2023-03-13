package com.depromeet.domain.usecase.member

import com.depromeet.domain.entity.MemberUpdateEntity
import com.depromeet.domain.entity.MemberUpdateRequestEntity
import com.depromeet.domain.repository.MemberRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(memberUpdateRequest: MemberUpdateRequestEntity): Flow<Result<MemberUpdateEntity>> {
        return memberRepository.updateMemberInfo(memberUpdateRequest)
    }
}