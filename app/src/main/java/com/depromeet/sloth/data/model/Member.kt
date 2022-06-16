package com.depromeet.sloth.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member (
    var email: String? = "",
    var memberId: Int? = 0,
    var memberName: String? = ""
): Parcelable