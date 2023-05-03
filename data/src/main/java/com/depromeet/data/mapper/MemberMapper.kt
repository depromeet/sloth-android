package com.depromeet.data.mapper

import com.depromeet.data.model.response.userprofile.UserProfileResponse
import com.depromeet.domain.entity.UserInfoEntity


internal fun UserProfileResponse.toEntity() =  UserInfoEntity(
    email = email,
    userId = userId,
    userName = userName,
    isEmailProvided = isEmailProvided,
    isPushAlarmUse = isPushAlarmUse,
    picture = picture
)
