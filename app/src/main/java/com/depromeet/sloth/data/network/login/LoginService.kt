package com.depromeet.sloth.data.network.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("api/oauth/login")
    suspend fun authorizeWithServer(@Body loginRequest: LoginRequest): Response<LoginResponse>?
}