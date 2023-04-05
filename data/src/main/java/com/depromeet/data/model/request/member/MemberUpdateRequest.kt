package com.depromeet.data.model.request.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateRequest (
    @SerialName("memberName")
    val memberName: String
)

