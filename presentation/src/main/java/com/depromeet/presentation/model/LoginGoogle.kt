package com.depromeet.presentation.model


data class LoginGoogle(
    val accessToken: String = "",
    val expiresIn: Int = 0,
    val scope: String = "",
    val tokenType: String = "",
    val idToken: String = "",
)
