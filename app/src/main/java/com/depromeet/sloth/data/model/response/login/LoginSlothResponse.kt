package com.depromeet.sloth.data.model.response.login

import com.google.gson.annotations.SerializedName


data class LoginSlothResponse(
    @SerializedName("accessToken")
    var accessToken: String = "",
    @SerializedName("accessTokenExpireTime")
    var accessTokenExpireTime: String = "",
    @SerializedName("refreshToken")
    var refreshToken: String = "",
    @SerializedName("refreshTokenExpireTime")
    var refreshTokenExpireTime: String = "",
    @SerializedName("isNewMember")
    var isNewMember: Boolean = false
)