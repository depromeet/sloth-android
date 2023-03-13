package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LoginGoogleRequestEntity
import com.depromeet.presentation.model.LoginGoogleRequest


internal fun LoginGoogleRequest.toEntity() = LoginGoogleRequestEntity(
    grant_type = grant_type,
    client_id = client_id,
    client_secret = client_secret,
    redirect_uri = redirect_uri,
    code = code
)

internal fun LoginGoogleRequestEntity.toUiModel() = LoginGoogleRequest(
    grant_type = grant_type,
    client_id = client_id,
    client_secret = client_secret,
    redirect_uri = redirect_uri,
    code = code
)