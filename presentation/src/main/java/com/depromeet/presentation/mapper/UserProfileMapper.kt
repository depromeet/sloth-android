package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.UserInfoEntity
import com.depromeet.presentation.model.UserProfile


internal fun UserInfoEntity.toUiModel() = UserProfile(
    email = email,
    userId = userId,
    userName = userName,
    isEmailProvided = isEmailProvided,
    isPushAlarmUse = isPushAlarmUse
)