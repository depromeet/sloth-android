package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.request.login.LoginSlothRequest
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SlothLoginService {
    @POST("api/oauth/login")
    suspend fun fetchSlothAuthInfo(
        @Header(KEY_AUTHORIZATION) accessToken: String?,
        @Body request: LoginSlothRequest
    ): Response<LoginSlothResponse>?
}