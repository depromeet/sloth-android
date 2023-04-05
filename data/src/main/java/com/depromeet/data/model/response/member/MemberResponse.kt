package com.depromeet.data.model.response.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MemberResponse(
    @SerialName("email")
    val email: String = "",
    @SerialName("memberId")
    val memberId: Int = 0,
    @SerialName("memberName")
    val memberName: String = "",
    @SerialName("isEmailProvided")
    val isEmailProvided: Boolean = false,
    @SerialName("isPushAlarmUse")
    val isPushAlarmUse: Boolean = false,
) {
    companion object {
        val EMPTY = MemberResponse("", 0, "", false, false)
    }
}

