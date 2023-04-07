package com.depromeet.domain.entity


data class LoginSlothEntity(
    val accessToken: String,
    val accessTokenExpireTime: String,
    val refreshToken: String,
    val refreshTokenExpireTime: String,
    val isNewUser: Boolean
)