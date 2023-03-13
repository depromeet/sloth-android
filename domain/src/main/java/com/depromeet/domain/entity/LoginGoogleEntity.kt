package com.depromeet.domain.entity


data class LoginGoogleEntity(
    val accessToken: String = "",
    val expiresIn: Int = 0,
    val scope: String = "",
    val tokenType: String = "",
    val idToken: String = "",
)