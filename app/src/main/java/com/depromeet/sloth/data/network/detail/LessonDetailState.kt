package com.depromeet.sloth.data.network.detail

import java.lang.Exception

sealed class LessonDetailState<out R> {
    data class Success<out T>(val data: T) : LessonDetailState<T>()
    data class Error(val exception: Exception) : LessonDetailState<Nothing>()
}