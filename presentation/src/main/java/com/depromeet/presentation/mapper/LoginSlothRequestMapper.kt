package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LoginSlothRequestEntity
import com.depromeet.presentation.model.LoginSlothRequest


internal fun LoginSlothRequest.toEntity() = LoginSlothRequestEntity(
    socialType = socialType
)

internal fun LoginSlothRequestEntity.toUiModel() = LoginSlothRequest(
    socialType = socialType
)