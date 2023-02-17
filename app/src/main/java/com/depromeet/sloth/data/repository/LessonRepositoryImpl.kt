package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateCountRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.*
import com.depromeet.sloth.data.network.service.LessonService
import com.depromeet.sloth.data.preferences.PreferenceManager
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.INTERNET_CONNECTION_ERROR
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */

// TODO 반복되는 함수 모듈화 (baseResponse 를 만들어서 모든 경우를 커버하도록)
// TODO State emit helper 함수 적용
class LessonRepositoryImpl @Inject constructor(
    private val preferences: PreferenceManager,
    private val lessonService: LessonService
) : LessonRepository {

    override fun fetchTodayLessonList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchTodayLessonList() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(TodayLessonResponse.EMPTY)))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun fetchLessonList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonList() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonListResponse.EMPTY)))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun finishLesson(lessonId: String) = flow {
        emit(Result.Loading)
        val response = lessonService.finishLesson(lessonId) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonFinishResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun updateLessonCount(count: Int, lessonId: Int) = flow {
        emit(Result.Loading)
        val response =
            lessonService.updateLessonCount(LessonUpdateCountRequest(count, lessonId)) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: UpdateLessonCountResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun fetchLessonDetail(lessonId: String) = flow {
        emit(Result.Loading)
        val response =
            lessonService.fetchLessonDetail(lessonId) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonDetailResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun registerLesson(lessonRegisterRequest: LessonRegisterRequest) = flow {
        emit(Result.Loading)
        val response =
            lessonService.registerLesson(lessonRegisterRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonRegisterResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun deleteLesson(lessonId: String) = flow {
        emit(Result.Loading)
        val response = lessonService.deleteLesson(lessonId) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonDeleteResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun fetchLessonCategoryList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonCategoryList() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonCategoryResponse.EMPTY)))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun fetchLessonSiteList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonSiteList() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonSiteResponse.EMPTY)))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }

    override fun updateLesson(lessonId: String, lessonUpdateRequest: LessonUpdateRequest) = flow {
        emit(Result.Loading)
        val response =
            lessonService.updateLesson(lessonId, lessonUpdateRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonUpdateResponse.EMPTY))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is IOException -> {
                    // Handle Internet Connection Error
                    emit(Result.Error(Exception(INTERNET_CONNECTION_ERROR)))
                }

                else -> {
                    // Handle Other Error
                    emit(Result.Error(throwable))
                }
            }
        }
}