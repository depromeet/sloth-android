package com.depromeet.data.model.response.login

import com.google.gson.annotations.SerializedName


data class LoginGoogleResponse(
    @SerializedName("access_token")
    val accessToken: String = "",
    @SerializedName("expires_in")
    val expiresIn: Int = 0,
    @SerializedName("scope")
    val scope: String = "",
    @SerializedName("token_type")
    val tokenType: String = "",
    @SerializedName("id_token")
    val idToken: String = "",
) {
    companion object {
        val EMPTY = LoginGoogleResponse("", 0, "","")
    }
}

