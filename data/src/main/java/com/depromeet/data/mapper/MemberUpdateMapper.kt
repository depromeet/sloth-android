package com.depromeet.data.mapper

import com.depromeet.data.model.response.member.MemberUpdateResponse
import com.depromeet.domain.entity.MemberUpdateEntity


internal fun MemberUpdateResponse.toEntity() = MemberUpdateEntity(
    memberName = memberName
)
