package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LoginSlothEntity
import com.depromeet.presentation.model.LoginSloth

internal fun LoginSloth.toEntity() = LoginSlothEntity(
    accessToken = accessToken,
    accessTokenExpireTime = accessTokenExpireTime,
    refreshToken = refreshToken,
    refreshTokenExpireTime = refreshTokenExpireTime,
    isNewMember = isNewMember
)

internal fun LoginSlothEntity.toUiModel() = LoginSloth(
    accessToken = accessToken,
    accessTokenExpireTime = accessTokenExpireTime,
    refreshToken = refreshToken,
    refreshTokenExpireTime = refreshTokenExpireTime,
    isNewMember = isNewMember
)