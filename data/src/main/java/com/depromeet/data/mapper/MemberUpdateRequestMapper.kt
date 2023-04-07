package com.depromeet.data.mapper

import com.depromeet.data.model.request.userprofile.UserProfileRequest
import com.depromeet.domain.entity.UserProfileUpdateRequestEntity


internal fun UserProfileUpdateRequestEntity.toModel() = UserProfileRequest(
    userName = userName
)