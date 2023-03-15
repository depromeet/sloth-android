package com.depromeet.data.model.response.login

import com.google.gson.annotations.SerializedName


data class LoginSlothResponse(
    @SerializedName("accessToken")
    val accessToken: String = "",
    @SerializedName("accessTokenExpireTime")
    val accessTokenExpireTime: String = "",
    @SerializedName("refreshToken")
    val refreshToken: String = "",
    @SerializedName("refreshTokenExpireTime")
    val refreshTokenExpireTime: String = "",
    @SerializedName("isNewMember")
    val isNewMember: Boolean = false
) {
    companion object {
        val EMPTY = LoginSlothResponse("", "", "", "", false)
    }
}

