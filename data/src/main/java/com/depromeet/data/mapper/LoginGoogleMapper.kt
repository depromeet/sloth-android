package com.depromeet.data.mapper

import com.depromeet.data.model.response.login.LoginGoogleResponse
import com.depromeet.domain.entity.LoginGoogleEntity


internal fun LoginGoogleResponse.toEntity() = LoginGoogleEntity(
    accessToken = accessToken,
    expiresIn = expiresIn,
    scope = scope,
    tokenType = tokenType,
    idToken = idToken
)
