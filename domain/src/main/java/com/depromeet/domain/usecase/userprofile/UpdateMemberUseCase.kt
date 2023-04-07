package com.depromeet.domain.usecase.userprofile

import com.depromeet.domain.entity.MemberUpdateEntity
import com.depromeet.domain.entity.MemberUpdateRequestEntity
import com.depromeet.domain.repository.UserProfileRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateMemberUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    operator fun invoke(memberUpdateRequest: MemberUpdateRequestEntity): Flow<Result<MemberUpdateEntity>> {
        return userProfileRepository.updateMemberInfo(memberUpdateRequest)
    }
}