package com.depromeet.data.mapper

import com.depromeet.data.model.response.member.MemberResponse
import com.depromeet.domain.entity.MemberEntity


internal fun MemberResponse.toEntity() =  MemberEntity(
    email = email,
    memberId = memberId,
    memberName = memberName,
    isEmailProvided = isEmailProvided,
    isPushAlarmUse = isPushAlarmUse
)
