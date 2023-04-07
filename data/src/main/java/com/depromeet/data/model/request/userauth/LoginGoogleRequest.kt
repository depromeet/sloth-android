package com.depromeet.data.model.request.userauth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginGoogleRequest(
    @SerialName("grant_type")
    val grant_type: String,
    @SerialName("client_id")
    val client_id: String,
    @SerialName("client_secret")
    val client_secret: String,
    @SerialName("redirect_uri")
    val redirect_uri: String,
    @SerialName("code")
    val code: String
)




