package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.request.login.LoginGoogleRequest
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GoogleLoginService {

    @POST("oauth2/v4/token")
    suspend fun fetchGoogleAuthInfo(
        @Body request: LoginGoogleRequest
    ): Response<LoginGoogleResponse>?
}