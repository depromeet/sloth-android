package com.depromeet.sloth.data.network.member

import com.google.gson.annotations.SerializedName

class MemberUpdateInfoRequest (
    @SerializedName("memberName")
    val memberName: String
)