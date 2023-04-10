package com.depromeet.data.source.remote

import android.util.Log
import com.depromeet.data.BuildConfig
import com.depromeet.data.mapper.toEntity
import com.depromeet.data.model.request.userauth.LoginGoogleRequest
import com.depromeet.data.model.request.userauth.LoginSlothRequest
import com.depromeet.data.model.response.userauth.LoginGoogleResponse
import com.depromeet.data.model.response.userauth.LoginSlothResponse
import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.source.remote.service.GoogleLoginService
import com.depromeet.data.source.remote.service.UserAuthService
import com.depromeet.data.util.DEFAULT_STRING_VALUE
import com.depromeet.data.util.GRANT_TYPE
import com.depromeet.data.util.INTERNET_CONNECTION_ERROR
import com.depromeet.data.util.VALUE_CONTENT_TYPE
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

class UserAuthRemoteDataSourceImpl @Inject constructor(
    private val userAuthService: UserAuthService,
    private val googleLoginService: GoogleLoginService,
    private val preferences: PreferenceManager,
) : UserAuthRemoteDataSource {

    override suspend fun checkLoginStatus(): Boolean {
        val accessToken = preferences.getAccessToken().first()
        val refreshToken = preferences.getRefreshToken().first()

        return accessToken != DEFAULT_STRING_VALUE && refreshToken != DEFAULT_STRING_VALUE
    }

    override fun googleLogin(authCode: String) = flow {
        emit(Result.Loading)
        val response = googleLoginService.googleLogin(
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
                emit(Result.Success(response.body()?.toEntity() ?: LoginGoogleResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun slothLogin(authToken: String, socialType: String) = flow {
        emit(Result.Loading)
        val response = userAuthService.slothLogin(
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
                preferences.registerAuthToken(accessToken, refreshToken)

                emit(Result.Success(response.body()?.toEntity() ?: LoginSlothResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            Log.d("slothLogin: ", "$throwable")
            Log.d("slothLogin: ", "${throwable.message}")
            when (throwable) {
//                is ConnectException -> {
//                    // Handle Other Error
//                    Log.d("fetchSlothAuthInfo: ", "ConnectException")
//                    emit(Result.Error(throwable))
//                }
                is IOException -> {
                    // Handle Internet Connection Error
                    Log.d("slothLogin: ", "IOException")
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun logout() = flow {
        emit(Result.Loading)

        val accessToken = runBlocking {
            preferences.getAccessToken().first()
        }
        val response = userAuthService.logout(VALUE_CONTENT_TYPE, accessToken) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> emit(Result.Success(response.body() ?: DEFAULT_STRING_VALUE))
            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }
}