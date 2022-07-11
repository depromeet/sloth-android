package com.depromeet.sloth.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Member (
    var email: String? = "",
    var memberId: Int? = 0,
    var memberName: String? = "",
    var isEmailProvided: Boolean? = false
): Parcelable {
    companion object {
        val EMPTY = Member("", 0, "", false)
    }
}