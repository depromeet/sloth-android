package com.depromeet.data.model.response.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateResponse(
    @SerialName("memberName")
    val memberName: String = ""
) {
    companion object {
        val EMPTY = MemberUpdateResponse("")
    }
}
