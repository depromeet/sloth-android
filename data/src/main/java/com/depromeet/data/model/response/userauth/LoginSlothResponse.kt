package com.depromeet.data.model.response.userauth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoginSlothResponse(
    @SerialName("accessToken")
    val accessToken: String = "",
    @SerialName("accessTokenExpireTime")
    val accessTokenExpireTime: String = "",
    @SerialName("refreshToken")
    val refreshToken: String = "",
    @SerialName("refreshTokenExpireTime")
    val refreshTokenExpireTime: String = "",
    @SerialName("isNewMember")
    val isNewUser: Boolean = false
) {
    companion object {
        val EMPTY = LoginSlothResponse("", "", "", "", false)
    }
}

