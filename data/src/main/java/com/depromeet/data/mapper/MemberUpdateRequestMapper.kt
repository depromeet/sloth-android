package com.depromeet.data.mapper

import com.depromeet.data.model.request.member.MemberUpdateRequest
import com.depromeet.domain.entity.MemberUpdateRequestEntity


internal fun MemberUpdateRequest.toEntity() = MemberUpdateRequestEntity(
    memberName = memberName
)

internal fun MemberUpdateRequestEntity.toModel() = MemberUpdateRequest(
    memberName = memberName
)