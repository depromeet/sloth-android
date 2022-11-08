package com.depromeet.sloth.data.model.request.login

import com.google.gson.annotations.SerializedName

data class LoginSlothRequest (
    @SerializedName("socialType")
    private val socialType: String
)