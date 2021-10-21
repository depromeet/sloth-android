package com.depromeet.sloth.data.network.login

/**
 *   LoginGoogleResponse
 *
 *  "access_token": "string",
 *  "expires_in": 0,
 *  "scope": "string",
 *  "token_type": "string"
 *  "id_token": "string"
 */

data class LoginGoogleResponse(
    var access_token: String = "",
    var expires_in: Int = 0,
    var scope: String = "",
    var token_type: String = "",
    var id_token: String = "",
)