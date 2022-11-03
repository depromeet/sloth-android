package com.depromeet.sloth.ui.common

/**
 * @author 최철훈
 * @created 2022-05-25
 * @desc API 요청에 대한 응답 상태
 */
//TODO RunCatching 도입
//TODO hideProgress 의 대한 고민
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    object UnLoading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Unauthorized(val throwable: Throwable) : UiState<Nothing>()
    data class Error(val throwable: Throwable) : UiState<Nothing>()
}