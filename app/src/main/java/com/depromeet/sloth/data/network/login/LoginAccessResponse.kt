package com.depromeet.sloth.data.network.login

data class LoginAccessResponse(
    val access_token: String,
    val expires_in: Int,
    val scope: String,
    val token_type: String,
    val id_token: String,
)