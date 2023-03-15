package com.depromeet.data.model.response.member

import com.google.gson.annotations.SerializedName


data class MemberUpdateResponse(
    @SerializedName("memberName")
    val memberName: String = ""
) {
    companion object {
        val EMPTY = MemberUpdateResponse("")
    }
}
