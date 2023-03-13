package com.depromeet.data.mapper

import com.depromeet.data.model.request.login.LoginSlothRequest
import com.depromeet.domain.entity.LoginSlothRequestEntity


internal fun LoginSlothRequest.toEntity() = LoginSlothRequestEntity(
    socialType = socialType
)

internal fun LoginSlothRequestEntity.toModel() = LoginSlothRequest(
    socialType = socialType
)