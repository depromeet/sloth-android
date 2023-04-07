package com.depromeet.data.model.request.userprofile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileRequest (
    @SerialName("memberName")
    val userName: String
)

