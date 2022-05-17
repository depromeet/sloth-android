package com.depromeet.sloth.data.network.lesson.detail

import java.lang.Exception

sealed class LessonDetailState<out R> {
    object Loading : LessonDetailState<Nothing>()
    data class Success<out T>(val data: T) : LessonDetailState<T>()
    data class Unauthorized(val exception: Exception) : LessonDetailState<Nothing>()
    data class Error(val exception: Exception) : LessonDetailState<Nothing>()
}