package com.depromeet.data.model.request.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LoginSlothRequest (
    @SerialName("socialType")
    val socialType: String
)

