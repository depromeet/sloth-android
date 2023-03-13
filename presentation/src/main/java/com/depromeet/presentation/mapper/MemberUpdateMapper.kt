package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.MemberUpdateEntity
import com.depromeet.presentation.model.MemberUpdate


internal fun MemberUpdate.toEntity() = MemberUpdateEntity(
    memberName = memberName
)

internal fun MemberUpdateEntity.toUiModel() = MemberUpdate(
    memberName = memberName
)