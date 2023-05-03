package com.depromeet.data.mapper

import com.depromeet.data.model.response.userprofile.UserProfileUpdateResponse
import com.depromeet.domain.entity.UserProfileUpdateEntity


internal fun UserProfileUpdateResponse.toEntity() = UserProfileUpdateEntity(
    userName = userName,
    profileImageUrl = profileImageUrl
)
