package com.depromeet.sloth.data.network.member


/**
 *  MemberInfoResponse
 *
 *  "email": "string",
 *  "memberId": 0,
 *  "memberName": "string"
 */

data class MemberInfoResponse (
    var email: String = "",
    var memberId: Int = 0,
    var memberName: String = ""
)