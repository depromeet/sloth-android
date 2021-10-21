package com.depromeet.sloth.data.network.login

import com.google.gson.annotations.SerializedName

class LoginRequest(
    @SerializedName("socialType")
    private val socialType: String
)