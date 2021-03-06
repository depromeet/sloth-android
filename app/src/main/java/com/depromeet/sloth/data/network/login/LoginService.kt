package com.depromeet.sloth.data.network.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("api/oauth/login")
    suspend fun fetchSlothAuthInfo(@Body request: LoginSlothRequest): Response<LoginSlothResponse>?

    @POST("oauth2/v4/token")
    suspend fun fetchGoogleAuthInfo(@Body request: LoginGoogleRequest): Response<LoginGoogleResponse>?
}