package com.depromeet.sloth.data.network.mypage

import com.google.gson.annotations.SerializedName

class MypageRequest (

    @SerializedName("memberName")
    private val memberName: String
)