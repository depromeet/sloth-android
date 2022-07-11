package com.depromeet.sloth.data.network.lesson


/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 응답 상태
 */

sealed class LessonState<out T> {
    object Loading : LessonState<Nothing>()
    object UnLoading : LessonState<Nothing>()
    data class Success<T>(val data: T) : LessonState<T>()
    data class Unauthorized(val throwable: Throwable) : LessonState<Nothing>()
    data class Error(val throwable: Throwable) : LessonState<Nothing>()
}