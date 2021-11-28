package com.depromeet.sloth.data.network.mypage


/**
 *  MypageResponse
 *
 *  "email": "string",
 *  "memberId": 0,
 *  "memberName": "string"
 */

data class MypageResponse (
    var email: String = "",
    var memberId: Int = 0,
    var memberName: String = ""
)