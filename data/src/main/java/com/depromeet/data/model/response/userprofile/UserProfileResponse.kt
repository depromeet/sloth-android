package com.depromeet.data.model.response.userprofile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserProfileResponse(
    @SerialName("email")
    val email: String = "",
    @SerialName("memberId")
    val userId: Int = 0,
    @SerialName("memberName")
    val userName: String = "",
    @SerialName("isEmailProvided")
    val isEmailProvided: Boolean = false,
    @SerialName("isPushAlarmUse")
    val isPushAlarmUse: Boolean = false,
) {
    companion object {
        val EMPTY = UserProfileResponse("", 0, "", false, false)
    }
}

