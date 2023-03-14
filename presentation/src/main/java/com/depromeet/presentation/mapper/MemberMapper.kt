package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.MemberEntity
import com.depromeet.presentation.model.Member


internal fun MemberEntity.toUiModel() =  Member(
    email = email,
    memberId = memberId,
    memberName = memberName,
    isEmailProvided = isEmailProvided,
    isPushAlarmUse = isPushAlarmUse
)