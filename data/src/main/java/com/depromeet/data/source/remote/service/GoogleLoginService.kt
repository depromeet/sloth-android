package com.depromeet.data.source.remote.service

import com.depromeet.data.model.request.userauth.LoginGoogleRequest
import com.depromeet.data.model.response.userauth.LoginGoogleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface GoogleLoginService {

    @POST("oauth2/v4/token")
    suspend fun googleLogin(
        @Body loginGoogleRequest: LoginGoogleRequest
    ): Response<LoginGoogleResponse>?
}