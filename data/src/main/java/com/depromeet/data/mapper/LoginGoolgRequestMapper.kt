package com.depromeet.data.mapper

import com.depromeet.data.model.request.login.LoginGoogleRequest
import com.depromeet.domain.entity.LoginGoogleRequestEntity


internal fun LoginGoogleRequest.toEntity() = LoginGoogleRequestEntity(
    grant_type = grant_type,
    client_id = client_id,
    client_secret = client_secret,
    redirect_uri = redirect_uri,
    code = code
)

internal fun LoginGoogleRequestEntity.toModel() = LoginGoogleRequest(
    grant_type = grant_type,
    client_id = client_id,
    client_secret = client_secret,
    redirect_uri = redirect_uri,
    code = code
)