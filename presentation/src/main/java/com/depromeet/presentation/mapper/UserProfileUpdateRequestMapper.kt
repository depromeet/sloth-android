package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.UserProfileUpdateRequestEntity
import com.depromeet.presentation.model.UserProfileUpdateRequest


internal fun UserProfileUpdateRequest.toEntity() = UserProfileUpdateRequestEntity(
    userName = userName
)