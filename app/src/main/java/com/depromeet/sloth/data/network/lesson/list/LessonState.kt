package com.depromeet.sloth.data.network.lesson.list

import com.depromeet.sloth.data.model.LessonSite
import java.lang.Exception

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson API 응답 상태
 */

sealed class LessonState<out R> {
    object Loading : LessonState<Nothing>()
    data class Success<out T>(val data: T) : LessonState<T>()
    data class Unauthorized(val exception: Exception) : LessonState<Nothing>()
    data class Error(val exception: Exception) : LessonState<Nothing>()
    object Forbidden : LessonState<Nothing>()
    object NotFound : LessonState<Nothing>()
}