package com.depromeet.sloth.data.network.lesson

import java.lang.Exception

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson API 응답 상태
 */
sealed class LessonState<out R> {
    data class Success<out T>(val data: T) : LessonState<T>()
    data class Error(val exception: Exception) : LessonState<Nothing>()
    object Unauthorized : LessonState<Nothing>()
    object Forbidden : LessonState<Nothing>()
    object NotFound : LessonState<Nothing>()
}