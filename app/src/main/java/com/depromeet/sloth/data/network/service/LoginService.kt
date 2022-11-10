package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.model.request.login.LoginGoogleRequest
import com.depromeet.sloth.data.model.request.login.LoginSlothRequest
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {
    @POST("api/oauth/login")
    suspend fun fetchSlothAuthInfo(
        @Header("Authorization") accessToken: String?,
        @Body request: LoginSlothRequest
    ): Response<LoginSlothResponse>?

    @POST("${BuildConfig.GOOGLE_BASE_URL}oauth2/v4/token")
    suspend fun fetchGoogleAuthInfo(
        @Body request: LoginGoogleRequest
    ): Response<LoginGoogleResponse>?
}