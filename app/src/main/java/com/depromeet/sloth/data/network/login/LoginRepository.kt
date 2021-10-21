package com.depromeet.sloth.data.network.login

import com.depromeet.sloth.data.network.*
import java.lang.Exception

class LoginRepository(
    private val generator: ServiceGenerator,
) {
    suspend fun getAuthTokens(accessToken: String, socialType: String): LoginState<LoginResponse> {

        generator.createService(
            LoginService::class.java,
            accessToken
        ).fetchAuthTokens(
            LoginRequest(socialType = socialType)
        )?.run {
            return LoginState.Success(
                this.body() ?: LoginResponse(
                    accessToken = "accessToken",
                    accessTokenExpireTime = "yyyy-MM-dd HH:mm:ss",
                    refreshToken = "refreshToken",
                    refreshTokenExpireTime = "yyyy-MM-dd HH:mm:ss"
                )
            )
        }

        return LoginState.Error(Exception("Retrofit Exception"))
    }

    suspend fun getAccessToken(serverAuthCode: String): LoginState<LoginAccessResponse> {

        generator.createGoogleService(
            LoginService::class.java,
            serverAuthCode
        ).fetchAccessToken(
            )?.run {
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
}

