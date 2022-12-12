package com.depromeet.sloth.data.repository

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.login.LoginGoogleRequest
import com.depromeet.sloth.data.model.request.login.LoginSlothRequest
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.data.network.service.GoogleLoginService
import com.depromeet.sloth.data.network.service.SlothLoginService
import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.domain.repository.LoginRepository
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.GRANT_TYPE
import retrofit2.Retrofit
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val preferences: Preferences,
    private val slothLoginService: SlothLoginService,
    private val googleLoginService: GoogleLoginService
) : LoginRepository {

    override fun checkLoggedIn(): Boolean {
        return preferences.getAccessToken()
            .isNotEmpty() && preferences.getRefreshToken().isNotEmpty()
    }

    override suspend fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String
    ): Result<LoginSlothResponse> {
        slothLoginService.fetchSlothAuthInfo(authToken, LoginSlothRequest(socialType = socialType))?.run {
            val accessToken = body()?.accessToken ?: DEFAULT_STRING_VALUE
            val refreshToken = body()?.refreshToken ?: DEFAULT_STRING_VALUE
            preferences.saveAuthToken(accessToken, refreshToken)

            return Result.Success(this.body() ?: LoginSlothResponse())
        } ?: return Result.Error(Exception("Login Exception"))
    }

//    override suspend fun fetchSlothAuthInfo(
//        authToken: String,
//        socialType: String
//    ) = flow {
//        emit(Result.Loading)
//        val response = slothLoginService.fetchSlothAuthInfo(
//            authToken,
//            LoginSlothRequest(socialType = socialType)
//        )
//            ?: run {
//                emit(Result.Error(Exception("Response is null")))
//                return@flow
//            }
//        when (response.code()) {
//            200 -> {
//                val accessToken = body()?.accessToken ?: DEFAULT_STRING_VALUE
//                val refreshToken = body()?.refreshToken ?: DEFAULT_STRING_VALUE
//                preferences.saveAuthToken(accessToken, refreshToken)
//
//                return Result.Success(this.body() ?: LoginSlothResponse())
//            }
//
//            else -> emit(Result.Error(Exception(response.message()), response.code()))
//        }
//    }
//        .catch { throwable -> emit(Result.Error(throwable)) }
//        .onCompletion { emit(Result.UnLoading) }

    override suspend fun fetchGoogleAuthInfo(
        authCode: String
    ): Result<LoginGoogleResponse> {
        Retrofit.Builder()
        googleLoginService.fetchGoogleAuthInfo(
            LoginGoogleRequest(
                grant_type = GRANT_TYPE,
                client_id = BuildConfig.GOOGLE_CLIENT_ID,
                client_secret = BuildConfig.GOOGLE_CLIENT_SECRET,
                redirect_uri = DEFAULT_STRING_VALUE,
                code = authCode
            )
        )?.run {
            return Result.Success(this.body() ?: LoginGoogleResponse())
        } ?: return Result.Error(Exception("Retrofit Exception"))
    }
}