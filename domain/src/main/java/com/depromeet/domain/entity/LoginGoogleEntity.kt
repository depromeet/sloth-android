package com.depromeet.domain.entity


data class LoginGoogleEntity(
    val accessToken: String,
    val expiresIn: Int,
    val scope: String,
    val tokenType: String,
    val idToken: String,
)