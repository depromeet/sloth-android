package com.depromeet.domain.entity


data class MemberEntity(
    val email: String,
    val memberId: Int,
    val memberName: String,
    val isEmailProvided: Boolean,
    val isPushAlarmUse: Boolean,
)