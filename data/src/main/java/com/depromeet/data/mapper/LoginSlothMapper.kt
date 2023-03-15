package com.depromeet.data.mapper

import com.depromeet.data.model.response.login.LoginSlothResponse
import com.depromeet.domain.entity.LoginSlothEntity

internal fun LoginSlothResponse.toEntity() = LoginSlothEntity(
    accessToken = accessToken,
    accessTokenExpireTime = accessTokenExpireTime,
    refreshToken = refreshToken,
    refreshTokenExpireTime = refreshTokenExpireTime,
    isNewMember = isNewMember
)