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
        ).authorizeWithServer(
            LoginRequest(socialType = socialType)
        )?.run {
            return LoginState.Success(
                this.body() ?: LoginResponse()
            )
        } ?: return LoginState.Error(Exception("Login Exception"))
    }
}