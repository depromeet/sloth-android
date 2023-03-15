package com.depromeet.data.mapper

import com.depromeet.data.model.request.member.MemberUpdateRequest
import com.depromeet.domain.entity.MemberUpdateRequestEntity


internal fun MemberUpdateRequestEntity.toModel() = MemberUpdateRequest(
    memberName = memberName
)