package com.depromeet.data.util

import com.depromeet.data.source.local.preferences.PreferenceManager
import retrofit2.Response
import com.depromeet.domain.util.Result

//TODO 제네릭의 대한 추가적인 학습 필요
suspend fun <T, R> Response<T>.handleResponse(
    preferences: PreferenceManager,
    successHandler: (Response<T>) -> R
): Result<R> {
    return when (this.code()) {
        HTTP_OK -> {
            val newAccessToken = this.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
            if (newAccessToken.isNotEmpty()) {
                preferences.updateAccessToken(newAccessToken)
            }
            Result.Success(successHandler(this))
        }

        else -> Result.Error(Exception(this.message()), this.code())
    }
}