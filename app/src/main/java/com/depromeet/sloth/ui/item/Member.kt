package com.depromeet.sloth.ui.item

data class Member(
    val email: String = "",
    val memberName: String = "",
    val isEmailProvided: Boolean = false,
    val isPushAlarmUse: Boolean = false,
)
