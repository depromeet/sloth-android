package com.depromeet.sloth.data.model.response.member

import com.google.gson.annotations.SerializedName


data class MemberUpdateResponse(
    @SerializedName("memberName")
    var memberName: String = ""
) {
    companion object {
        val EMPTY = MemberUpdateResponse("")
    }
}
