package com.depromeet.data.model.request.login

import com.google.gson.annotations.SerializedName


data class LoginGoogleRequest(
    @SerializedName("grant_type")
    val grant_type: String,
    @SerializedName("client_id")
    val client_id: String,
    @SerializedName("client_secret")
    val client_secret: String,
    @SerializedName("redirect_uri")
    val redirect_uri: String,
    @SerializedName("code")
    val code: String
)




