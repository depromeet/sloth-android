package com.depromeet.sloth.data.network.login

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import javax.inject.Inject

class LoginRepository @Inject constructor() {
    suspend fun fetchSlothAuthInfo(
        accessToken: String,
        socialType: String
    ): LoginState<LoginSlothResponse> {
        RetrofitServiceGenerator.build(accessToken)
            .create(LoginService::class.java)
            .fetchSlothAuthInfo(LoginSlothRequest(socialType))?.run {
                return LoginState.Success(
                    this.body() ?: LoginSlothResponse()
                )
            } ?: return LoginState.Error(Exception("Login Exception"))
    }

    suspend fun fetchGoogleAuthInfo(
        authCode: String
    ): LoginState<LoginGoogleResponse> {
        RetrofitServiceGenerator.build(isGoogleLogin = true)
            .create(LoginService::class.java)
            .fetchGoogleAuthInfo(
                LoginGoogleRequest(
                    grant_type = "authorization_code",
                    client_id = BuildConfig.GOOGLE_CLIENT_ID,
                    client_secret = BuildConfig.GOOGLE_CLIENT_SECRET,
                    redirect_uri = "",
                    code = authCode
                )
            )?.run {
                return LoginState.Success(
                    this.body() ?: LoginGoogleResponse()
                )
            } ?: return LoginState.Error(Exception("Retrofit Exception"))
    }
}