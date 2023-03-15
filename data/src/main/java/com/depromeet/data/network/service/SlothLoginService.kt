package com.depromeet.data.network.service

import com.depromeet.data.model.request.login.LoginSlothRequest
import com.depromeet.data.model.response.login.LoginSlothResponse
import com.depromeet.data.util.KEY_AUTHORIZATION
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface SlothLoginService {
    @POST("api/oauth/login")
    suspend fun fetchSlothAuthInfo(
        @Header(KEY_AUTHORIZATION) accessToken: String?,
        @Body loginSlothRequest: LoginSlothRequest
    ): Response<LoginSlothResponse>?
}