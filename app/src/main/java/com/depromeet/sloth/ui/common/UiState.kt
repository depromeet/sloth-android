package com.depromeet.sloth.ui.common

/**
 * @author 최철훈
 * @created 2022-05-25
 * @desc API 요청에 대한 응답 상태
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object UnLoading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Unauthorized(val throwable: Throwable) : UiState<Nothing>()
    data class Error(val throwable: Throwable) : UiState<Nothing>()
}

//fun <T> UiState<T>.successOrNull(): T? = if (this is UiState.Success<T>) {
//    data
//} else {
//    null
//}
//
//fun <T> UiState<T>.throwableOrNull(): Throwable? = if (this is UiState.Error) {
//    throwable
//} else {
//    null
//}