package com.depromeet.sloth.data.network.lesson.delete

import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import java.lang.Exception

sealed class LessonDeleteState<out R> {
    object Loading : LessonDeleteState<Nothing>()
    data class Success<out T>(val data: T) : LessonDeleteState<T>()
    object NoContent : LessonDeleteState<Nothing>()
    data class Unauthorized(val exception: Exception) : LessonDeleteState<Nothing>()
    object Forbidden : LessonDeleteState<Nothing>()
    data class Error(val exception: Exception) : LessonDeleteState<Nothing>()
    object Finish : LessonDeleteState<Nothing>()
}