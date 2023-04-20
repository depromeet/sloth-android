package com.depromeet.data.service

import com.depromeet.data.model.request.userauth.LoginSlothRequest
import com.depromeet.data.model.response.userauth.LoginSlothResponse
import com.depromeet.data.util.KEY_AUTHORIZATION
import com.depromeet.data.util.KEY_CONTENT_TYPE
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST


interface UserAuthService {
    @POST("api/oauth/login")
    suspend fun slothLogin(
        @Header(KEY_AUTHORIZATION) accessToken: String,
        @Body loginSlothRequest: LoginSlothRequest
    ): Response<LoginSlothResponse>?

    @POST("api/logout")
    suspend fun logout(
        @Header(KEY_CONTENT_TYPE) contentType: String,
        @Header(KEY_AUTHORIZATION) accessToken: String,
    ): Response<String>?

    @PATCH("/api/member/delete")
    suspend fun withdraw(
        @Header(KEY_CONTENT_TYPE) contentType: String,
        @Header(KEY_AUTHORIZATION) accessToken: String,
    ): Response<String>?
}