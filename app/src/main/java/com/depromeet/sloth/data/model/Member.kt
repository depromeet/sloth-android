package com.depromeet.sloth.data.model

import androidx.annotation.Keep

@Keep
data class Member(
    var email: String = "",
    var memberId: Int = 0,
    var memberName: String = "",
    var isEmailProvided: Boolean = false,
    var isPushAlarmUse: Boolean = false,
) {
    companion object {
        val EMPTY = Member("", 0, "", false, false)
    }
}