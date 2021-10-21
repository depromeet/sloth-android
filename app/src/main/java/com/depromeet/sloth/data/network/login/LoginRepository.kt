package com.depromeet.sloth.data.network.login

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.ServiceGenerator.createService

class LoginRepository {
    suspend fun login(accessToken: String, socialType: String): LoginState<LoginResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.TARGET_SERVER,
            authToken = accessToken
        ).createService(
            serviceClass = LoginService::class.java,
        ).fetchAuthTokens(
            LoginRequest(socialType = socialType)
        )?.run {
            return LoginState.Success(
                this.body() ?: LoginResponse()
            )
        } ?: return LoginState.Error(Exception("Login Exception"))
    }

    suspend fun getAccessToken(serverAuthCode: String): LoginState<LoginAccessResponse> {
        ServiceGenerator.createGoogleService(
            LoginService::class.java,
            serverAuthCode
        ).fetchAccessToken()?.run {
            return LoginState.Success(
                this.body() ?: LoginAccessResponse(
                    access_token = "access_token",
                    expires_in = 0,
                    scope = "scope",
                    token_type = "token_type",
                    id_token = "id_token",
                )
            )
        }
        return LoginState.Error(Exception("Retrofit Exception"))
    }

    suspend fun getAuthTokens(accessToken: String, socialType: String): LoginState<LoginResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.TARGET_SERVER,
            authToken = accessToken
        ).createService(
            LoginService::class.java
        ).fetchAuthTokens(
            LoginRequest(socialType = socialType)
        )?.run {
            return LoginState.Success(
                this.body() ?: LoginResponse()
            )
        }

        return LoginState.Error(Exception("Retrofit Exception"))
    }
}