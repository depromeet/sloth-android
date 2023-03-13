package com.depromeet.domain.entity


data class LoginGoogleRequestEntity(
    val grant_type: String,
    val client_id: String,
    val client_secret: String,
    val redirect_uri: String,
    val code: String
)


