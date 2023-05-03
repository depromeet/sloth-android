package com.depromeet.presentation.model


data class UserProfile(
    val email: String = "",
    val userId: Int = 0,
    val userName: String = "",
    val isEmailProvided: Boolean = false,
    val isPushAlarmUse: Boolean = false,
    val picture: String? = ""
)

