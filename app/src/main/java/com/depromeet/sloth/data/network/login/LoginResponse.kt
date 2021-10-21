package com.depromeet.sloth.data.network.login

data class LoginResponse(
    val accessToken: String,
    val accessTokenExpireTime: String,
    val refreshToken: String,
    val refreshTokenExpireTime: String
)