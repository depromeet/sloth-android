package com.depromeet.sloth.ui.base

/**
 * @author 최철훈
 * @created 2022-05-25
 * @desc API 요청에 대한 응답 상태
 */
sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    object UnLoading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Unauthorized(val throwable: Throwable) : UIState<Nothing>()
    data class Error(val throwable: Throwable) : UIState<Nothing>()
}

fun <T> UIState<T>.successOrNull(): T? = if (this is UIState.Success<T>) {
    data
} else {
    null
}

fun <T> UIState<T>.throwableOrNull(): Throwable? = if (this is UIState.Error) {
    throwable
} else {
    null
}