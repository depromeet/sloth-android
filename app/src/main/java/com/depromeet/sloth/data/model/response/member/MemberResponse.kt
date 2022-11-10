package com.depromeet.sloth.data.model.response.member

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MemberResponse(
    @SerializedName("email")
    var email: String = "",
    @SerializedName("memberId")
    var memberId: Int = 0,
    @SerializedName("memberName")
    var memberName: String = "",
    @SerializedName("isEmailProvided")
    var isEmailProvided: Boolean = false,
    @SerializedName("isPushAlarmUse")
    var isPushAlarmUse: Boolean = false,
) {
    companion object {
        val EMPTY = MemberResponse("", 0, "", false, false)
    }
}