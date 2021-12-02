package com.depromeet.sloth.data.network.detail

import com.depromeet.sloth.data.network.home.LessonState
import java.lang.Exception

sealed class LessonDetailState<out R> {

    data class Success<out T>(val data: T) : LessonDetailState<T>()
    data class Error(val exception: Exception) : LessonDetailState<Nothing>()
    object Unauthorized : LessonDetailState<Nothing>()
    object Forbidden : LessonDetailState<Nothing>()
    object NotFound : LessonDetailState<Nothing>()

}