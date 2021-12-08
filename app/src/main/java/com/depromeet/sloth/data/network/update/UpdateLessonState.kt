package com.depromeet.sloth.data.network.update

import java.lang.Exception

sealed class UpdateLessonState<out R> {
    data class Success<out T>(val data: T): UpdateLessonState<T>()
    data class Error(val exception: Exception): UpdateLessonState<Nothing>()
    object NoContent: UpdateLessonState<Nothing>()
    object Unauthorized : UpdateLessonState<Nothing>()
    object Forbidden : UpdateLessonState<Nothing>()
}