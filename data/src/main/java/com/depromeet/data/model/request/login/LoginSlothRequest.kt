package com.depromeet.data.model.request.login

import com.google.gson.annotations.SerializedName


data class LoginSlothRequest (
    @SerializedName("socialType")
    val socialType: String
)

