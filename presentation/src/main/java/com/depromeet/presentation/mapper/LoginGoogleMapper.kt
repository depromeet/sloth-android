package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LoginGoogleEntity
import com.depromeet.presentation.model.LoginGoogle


internal fun LoginGoogle.toEntity() = LoginGoogleEntity(
    accessToken = accessToken,
    expiresIn = expiresIn,
    scope = scope,
    tokenType = tokenType,
    idToken = idToken
)

internal fun LoginGoogleEntity.toUiModel() = LoginGoogle(
    accessToken = accessToken,
    expiresIn = expiresIn,
    scope = scope,
    tokenType = tokenType,
    idToken = idToken
)