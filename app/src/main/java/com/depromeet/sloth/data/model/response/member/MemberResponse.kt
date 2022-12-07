package com.depromeet.sloth.data.model.response.member

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemberResponse(
    @SerializedName("email")
    val email: String = "",
    @SerializedName("memberId")
    val memberId: Int = 0,
    @SerializedName("memberName")
    val memberName: String = "",
    @SerializedName("isEmailProvided")
    val isEmailProvided: Boolean = false,
    @SerializedName("isPushAlarmUse")
    val isPushAlarmUse: Boolean = false,
) {
    companion object {
        val EMPTY = MemberResponse("", 0, "", false, false)
    }
}