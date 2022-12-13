package com.depromeet.sloth.data.repository

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.model.request.login.LoginGoogleRequest
import com.depromeet.sloth.data.model.request.login.LoginSlothRequest
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.data.network.service.GoogleLoginService
import com.depromeet.sloth.data.network.service.SlothLoginService
import com.depromeet.sloth.data.preferences.PreferenceManager
import com.depromeet.sloth.domain.repository.LoginRepository
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.GRANT_TYPE
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val preferences: PreferenceManager,
    private val slothLoginService: SlothLoginService,
    private val googleLoginService: GoogleLoginService
) : LoginRepository {

    override suspend fun checkLoggedIn(): Boolean {
        val accessToken = preferences.getAccessToken().first()
        val refreshToken = preferences.getRefreshToken().first()

        return accessToken != DEFAULT_STRING_VALUE && refreshToken != DEFAULT_STRING_VALUE
    }

    override fun fetchGoogleAuthInfo(
        authCode: String
    ) = flow {
        emit(Result.Loading)
        val response = googleLoginService.fetchGoogleAuthInfo(
            LoginGoogleRequest(
                grant_type = GRANT_TYPE,
                client_id = BuildConfig.GOOGLE_CLIENT_ID,
                client_secret = BuildConfig.GOOGLE_CLIENT_SECRET,
                redirect_uri = DEFAULT_STRING_VALUE,
                code = authCode
            )
        ) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                emit(Result.Success(response.body() ?: LoginGoogleResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun fetchSlothAuthInfo(
        authToken: String,
        socialType: String
    ) = flow {
        emit(Result.Loading)
        val response = slothLoginService.fetchSlothAuthInfo(
            authToken,
            LoginSlothRequest(socialType = socialType)
        ) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val accessToken = response.body()?.accessToken ?: DEFAULT_STRING_VALUE
                val refreshToken = response.body()?.refreshToken ?: DEFAULT_STRING_VALUE
                preferences.saveAuthToken(accessToken, refreshToken)

                emit(Result.Success(response.body() ?: LoginSlothResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }
}

