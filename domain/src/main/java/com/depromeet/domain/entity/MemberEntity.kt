package com.depromeet.domain.entity


data class MemberEntity(
    val email: String = "",
    val memberId: Int = 0,
    val memberName: String = "",
    val isEmailProvided: Boolean = false,
    val isPushAlarmUse: Boolean = false,
)