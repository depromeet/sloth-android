package com.depromeet.sloth.presentation.item

//TODO item data class 들을 뷰모델 내부에 uiState 로
data class Member(
    val email: String = "",
    val memberName: String = "",
    val isEmailProvided: Boolean = false,
    val isPushAlarmUse: Boolean = false,
)
