package com.depromeet.data.model.response.userprofile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileUpdateResponse(
    @SerialName("memberName")
    val userName: String = "",
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = ""
) {
    companion object {
        val EMPTY = UserProfileUpdateResponse("", "")
    }
}
