package com.depromeet.sloth.data.model.request.member

import com.google.gson.annotations.SerializedName

data class MemberUpdateRequest (
    @SerializedName("memberName")
    val memberName: String
)