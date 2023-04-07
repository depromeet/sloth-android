package com.depromeet.data.mapper

import com.depromeet.data.model.response.userauth.LoginSlothResponse
import com.depromeet.domain.entity.LoginSlothEntity

internal fun LoginSlothResponse.toEntity() = LoginSlothEntity(
    accessToken = accessToken,
    accessTokenExpireTime = accessTokenExpireTime,
    refreshToken = refreshToken,
    refreshTokenExpireTime = refreshTokenExpireTime,
    isNewUser = isNewUser
)