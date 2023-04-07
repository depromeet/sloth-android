package com.depromeet.domain.entity


data class UserInfoEntity(
    val email: String,
    val userId: Int,
    val userName: String,
    val isEmailProvided: Boolean,
    val isPushAlarmUse: Boolean,
)