package com.depromeet.sloth.data.network.login

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {

    suspend fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String
    ): LoginState<LoginSlothResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(authToken)
            .create(LoginService::class.java)
            .fetchSlothAuthInfo(
                LoginSlothRequest(
                    socialType = socialType
                )
            )?.run {
                val accessToken = body()?.accessToken ?: ""
                val refreshToken = body()?.refreshToken ?: ""
                preferenceManager.putAuthToken(accessToken, refreshToken)

                return LoginState.Success(this.body() ?: LoginSlothResponse())
            } ?: return LoginState.Error(Exception("Login Exception"))
    }

    suspend fun fetchGoogleAuthInfo(
        authCode: String
    ): LoginState<LoginGoogleResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(isGoogleLogin = true)
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
                return LoginState.Success(this.body() ?: LoginGoogleResponse())
            } ?: return LoginState.Error(Exception("Retrofit Exception"))
    }
}