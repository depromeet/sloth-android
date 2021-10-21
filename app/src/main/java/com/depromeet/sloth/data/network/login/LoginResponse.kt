package com.depromeet.sloth.data.network.login

/**
 *   LoginRespoonse
 *
 *  "accessToken": "string",
 *  "accessTokenExpireTime": "yyyy-MM-dd HH:mm:ss",
 *  "refreshToken": "string",
 *  "refreshTokenExpireTime": "yyyy-MM-dd HH:mm:ss"
 */

data class LoginResponse(
    var accessToken: String = "",
    var accessTokenExpireTime: String = "",
    var refreshToken: String = "",
    var refreshTokenExpireTime: String = ""
)