package com.depromeet.domain.usecase.member

import com.depromeet.domain.entity.MemberEntity
import com.depromeet.domain.repository.MemberRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchMemberInfoUseCase @Inject constructor (
    private val memberRepository: MemberRepository
) {
    operator fun invoke(): Flow<Result<MemberEntity>> {
        return memberRepository.fetchMemberInfo()
    }
}