package com.depromeet.sloth.data.network.register

import com.google.gson.annotations.SerializedName

class RegisterNicknameRequest (

    @SerializedName("memberName")
    private val memberName: String
)