package com.depromeet.sloth.data.network.member

/**
 * MemberInfoResponse
 *
 * memberId: Int,
 * memberName: "String",
 * email: "String
 */

data class MemberInfoResponse(
    var memberId: Int = 0,
    var memberName: String = "",
    var email: String = ""
)
