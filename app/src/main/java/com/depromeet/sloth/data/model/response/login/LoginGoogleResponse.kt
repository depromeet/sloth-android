package com.depromeet.sloth.data.model.response.login

import com.google.gson.annotations.SerializedName


data class LoginGoogleResponse(
    @SerializedName("access_token")
    var accessToken: String = "",
    @SerializedName("expires_in")
    var expiresIn: Int = 0,
    @SerializedName("scope")
    var scope: String = "",
    @SerializedName("token_type")
    var tokenType: String = "",
    @SerializedName("id_token")
    var idToken: String = "",
)