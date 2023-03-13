package com.depromeet.presentation.model


data class LoginSloth(
    val accessToken: String = "",
    val accessTokenExpireTime: String = "",
    val refreshToken: String = "",
    val refreshTokenExpireTime: String = "",
    val isNewMember: Boolean = false
)

