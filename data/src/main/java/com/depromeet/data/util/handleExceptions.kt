package com.depromeet.data.util

import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.net.ConnectException
import java.net.SocketTimeoutException

fun <T> Flow<Result<T>>.handleExceptions(): Flow<Result<T>> {
    return this.catch { throwable ->
        when (throwable) {
            is SocketTimeoutException -> {
                // Server Connection Error
                emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
            }
            is ConnectException -> {
                // Internet Connection Error
                emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
            }
            else -> {
                // Handle Other Error
                emit(Result.Error(throwable))
            }
        }
    }
}