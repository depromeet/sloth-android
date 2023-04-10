package com.depromeet.data.source.remote

import com.depromeet.data.mapper.toEntity
import com.depromeet.data.mapper.toModel
import com.depromeet.data.model.request.lesson.LessonUpdateCountRequest
import com.depromeet.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.data.model.response.lesson.LessonDetailResponse
import com.depromeet.data.model.response.lesson.LessonFinishResponse
import com.depromeet.data.model.response.lesson.LessonRegisterResponse
import com.depromeet.data.model.response.lesson.LessonStatisticsResponse
import com.depromeet.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.data.model.response.lesson.UpdateLessonCountResponse
import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.source.remote.service.LessonService
import com.depromeet.data.util.DEFAULT_STRING_VALUE
import com.depromeet.data.util.HTTP_OK
import com.depromeet.data.util.INTERNET_CONNECTION_ERROR
import com.depromeet.data.util.KEY_AUTHORIZATION
import com.depromeet.data.util.RESPONSE_NULL_ERROR
import com.depromeet.data.util.SERVER_CONNECTION_ERROR
import com.depromeet.domain.entity.LessonCategoryEntity
import com.depromeet.domain.entity.LessonEntity
import com.depromeet.domain.entity.LessonRegisterRequestEntity
import com.depromeet.domain.entity.LessonSiteEntity
import com.depromeet.domain.entity.LessonUpdateRequestEntity
import com.depromeet.domain.entity.TodayLessonEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.ConnectException
import javax.inject.Inject

// TODO 반복되는 함수 모듈화
class LessonRemoteDataSourceImpl @Inject constructor(
    private val lessonService: LessonService,
    private val preferences: PreferenceManager
): LessonRemoteDataSource {
    override fun fetchTodayLessonList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchTodayLessonList() ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: emptyList<TodayLessonEntity>()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: emptyList<LessonEntity>()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: LessonFinishResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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
                emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                return@flow
            }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: UpdateLessonCountResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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
                emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                return@flow
            }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: LessonDetailResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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

    override fun registerLesson(lessonRegisterRequestEntity: LessonRegisterRequestEntity) = flow {
        emit(Result.Loading)
        val response =
            lessonService.registerLesson(lessonRegisterRequestEntity.toModel())
                ?: run {
                    emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                    return@flow
                }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: LessonRegisterResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: LessonDeleteResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: emptyList<LessonCategoryEntity>()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: emptyList<LessonSiteEntity>()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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

    override fun updateLesson(lessonId: String, lessonUpdateRequestEntity: LessonUpdateRequestEntity) = flow {
        emit(Result.Loading)
        val response =
            lessonService.updateLesson(lessonId, lessonUpdateRequestEntity.toModel())
                ?: run {
                    emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                    return@flow
                }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: LessonUpdateResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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

    override fun fetchLessonStatisticsInformation() = flow {
        emit(Result.Loading)
        val response =
            lessonService.fetchLessonStatisticsInformation()
                ?: run {
                    emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                    return@flow
                }
        when (response.code()) {
            HTTP_OK -> {
                val newAccessToken = response.headers()[KEY_AUTHORIZATION] ?: DEFAULT_STRING_VALUE
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body()?.toEntity() ?: LessonStatisticsResponse.EMPTY.toEntity()))
            }

            else -> emit(Result.Error(Exception(response.message()), response.code()))
        }
    }
        .catch { throwable ->
            when (throwable) {
                is ConnectException -> {
                    // Handle Server Connection Error
                    emit(Result.Error(Exception(SERVER_CONNECTION_ERROR)))
                }

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