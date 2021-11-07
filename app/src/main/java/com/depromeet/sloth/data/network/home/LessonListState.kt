package com.depromeet.sloth.data.network.home

import java.lang.Exception

sealed class LessonListState<out R> {
    data class Success<out T>(val data: T): LessonListState<T>()
    data class Error(val exception: Exception): LessonListState<Nothing>()
    object Unauthorized : LessonListState<Nothing>()
    object Forbidden : LessonListState<Nothing>()
    object NotFound : LessonListState<Nothing>()
}