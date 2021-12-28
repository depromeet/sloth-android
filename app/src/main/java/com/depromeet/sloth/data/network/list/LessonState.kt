package com.depromeet.sloth.data.network.list

import java.lang.Exception

sealed class LessonState<out R> {
    data class Success<out T>(val data: T): LessonState<T>()
    data class Error(val exception: Exception): LessonState<Nothing>()
    object Unauthorized : LessonState<Nothing>()
    object Forbidden : LessonState<Nothing>()
    object NotFound : LessonState<Nothing>()
}