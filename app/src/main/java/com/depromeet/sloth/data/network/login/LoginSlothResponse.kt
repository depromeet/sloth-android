package com.depromeet.sloth.data.network.login

/**
 *   LoginResponse
 *
 *  "accessToken": "string",
 *  "accessTokenExpireTime": "yyyy-MM-dd HH:mm:ss",
 *  "refreshToken": "string",
 *  "refreshTokenExpireTime": "yyyy-MM-dd HH:mm:ss"
 */

data class LoginSlothResponse(
    var accessToken: String = "",
    var accessTokenExpireTime: String = "",
    var refreshToken: String = "",
    var refreshTokenExpireTime: String = ""
)