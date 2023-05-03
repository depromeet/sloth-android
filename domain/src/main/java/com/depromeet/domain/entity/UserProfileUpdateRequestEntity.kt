package com.depromeet.domain.entity


data class UserProfileUpdateRequestEntity (
    val userName: String,
    val profileImageUrl: String?
)