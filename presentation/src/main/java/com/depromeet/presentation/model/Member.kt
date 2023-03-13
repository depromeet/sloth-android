package com.depromeet.presentation.model


data class Member(
    val email: String = "",
    val memberId: Int = 0,
    val memberName: String = "",
    val isEmailProvided: Boolean = false,
    val isPushAlarmUse: Boolean = false,
)

