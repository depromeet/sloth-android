package com.depromeet.sloth.common

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

////유의 사항 : 코틀린에서 기본적으로 제공하는 Result 와 다르다
//sealed interface Result<out T> {
//    data class Success<T>(val data: T) : Result<T>
//    data class Error(val exception: Throwable? = null) : Result<Nothing>
//    object Loading : Result<Nothing>
//}
//
//// 반복되는 코드를 제거하기 위해 helper 함수 추카
//fun <T> Flow<T>.asResult(): Flow<Result<T>> =
//    this
//        // 맵의 결과값이 Success 만 들어오는 것이 아니기때문에
//        // 코드 분석 필요
//        .map<T, Result<T>> {
//            Result.Success(it)
//        }
//        .onStart { emit(Result.Loading) }
//        .catch { Result.Error(it) }

