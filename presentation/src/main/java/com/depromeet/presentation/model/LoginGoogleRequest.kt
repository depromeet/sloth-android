package com.depromeet.presentation.model


data class LoginGoogleRequest(
    val grant_type: String,
    val client_id: String,
    val client_secret: String,
    val redirect_uri: String,
    val code: String
)


