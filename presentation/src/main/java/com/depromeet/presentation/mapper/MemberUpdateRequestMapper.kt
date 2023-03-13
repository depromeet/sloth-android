package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.MemberUpdateRequestEntity
import com.depromeet.presentation.model.MemberUpdateRequest


internal fun MemberUpdateRequest.toEntity() = MemberUpdateRequestEntity(
    memberName = memberName
)

internal fun MemberUpdateRequestEntity.toUiModel() = MemberUpdateRequest(
    memberName = memberName
)