package com.depromeet.domain.util


sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable, val statusCode: Int? = 0) : Result<Nothing>()
}

// now in android Result class
////유의 사항 : 코틀린에서 기본적으로 제공하는 Result 와 다르다
//sealed interface Result<out T> {
//    data class Success<T>(val data: T) : Result<T>
//    data class Error(val exception: Throwable? = null) : Result<Nothing>
//    object Loading : Result<Nothing>
//}


// result 를 repository interface 함수의 반환형에서 제거하면 해당 함수 적용 가능
// 반복되는 코드를 제거하기 위해 helper 함수 추카
//fun <T> Flow<T>.asResult(): Flow<Result<T>> {
//    return this
//        // 맵의 결과값이 Success 만 들어오는 것이 아니기때문에
//        // 코드 분석 필요
//        .map<T, Result<T>> {
//            Result.Success(it)
//        }
//        .onStart { emit(Result.Loading) }
//        .catch { Result.Error(it) }
//}


//fun <T> safeFlow(apiFunc: suspend () -> T): Flow<Result<T>> = flow {
//    try {
//        emit(Result.Success(apiFunc.invoke()))
//    } catch (e: HttpException) {
//        emit(Result.Error(code = e.code(), message = e.stackTraceToString()))
//    } catch (e: Exception) {
//        emit(Result.Exception(e))
//    } catch (e: Throwable) {
//        emit(Result.Exception(e))
//    }
//}
