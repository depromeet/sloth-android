package com.depromeet.sloth.data.network.lesson.update

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc
 */
import java.lang.Exception

sealed class LessonUpdateState<out R> {
    object Loading : LessonUpdateState<Nothing>()
    data class Success<out T>(val data: T) : LessonUpdateState<T>()
    data class Unauthorized(val exception: Exception) : LessonUpdateState<Nothing>()
    data class Error(val exception: Exception) : LessonUpdateState<Nothing>()
    object NoContent : LessonUpdateState<Nothing>()
    object Forbidden : LessonUpdateState<Nothing>()
}