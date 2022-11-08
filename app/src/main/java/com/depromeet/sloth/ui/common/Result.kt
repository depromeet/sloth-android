package com.depromeet.sloth.ui.common

/**
 * @author 최철훈
 * @created 2022-05-25
 * @desc API 요청에 대한 응답 상태
 */
//TODO RunCatching 도입
//TODO hideProgress 의 대한 고민
sealed class Result<out T> {
    object Loading : Result<Nothing>()
    object UnLoading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Unauthorized(val throwable: Throwable) : Result<Nothing>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
}

