package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateCountRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.data.model.response.lesson.LessonFinishResponse
import com.depromeet.sloth.data.model.response.lesson.LessonRegisterResponse
import com.depromeet.sloth.data.model.response.lesson.LessonSiteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.sloth.data.network.service.LessonService
import com.depromeet.sloth.data.preferences.Preferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */

// TODO HTTP 코드 응답 처리 함수화
// TODO State 방출 helper 함수 만들기
class LessonRepositoryImpl @Inject constructor(
    private val preferences: Preferences,
    private val service: LessonService
) : LessonRepository {

    override fun fetchTodayLessonList() = flow {
        emit(Result.Loading)
        val response = service.fetchTodayLessonList(preferences.getAccessToken()) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonTodayResponse.EMPTY)))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun fetchAllLessonList() = flow {
        emit(Result.Loading)
        val response = service.fetchAllLessonList(preferences.getAccessToken()) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonAllResponse.EMPTY)))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Error(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun finishLesson(lessonId: String) = flow {
        emit(Result.Loading)
        val response = service.finishLesson(preferences.getAccessToken(), lessonId) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonFinishResponse.EMPTY))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Error(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override suspend fun updateLessonCount(
        count: Int,
        lessonId: Int,
    ): Result<LessonUpdateCountResponse> {
        service.updateLessonCount(
            preferences.getAccessToken(),
            LessonUpdateCountRequest(count, lessonId)
        )?.run {
            return when (this.code()) {
                200 -> {
                    val newAccessToken = headers()["Authorization"] ?: ""
                    if (newAccessToken.isNotEmpty()) {
                        preferences.updateAccessToken(newAccessToken)
                    }
                    Result.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                }
                401 -> {
                    preferences.removeAuthToken()
                    Result.Error(Exception(message()))
                }
                else -> Result.Error(Exception(message()))
            }
        } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override fun fetchLessonDetail(lessonId: String) = flow {
        emit(Result.Loading)
        val response =
            service.fetchLessonDetail(preferences.getAccessToken(), lessonId) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonDetailResponse.EMPTY))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun registerLesson(lessonRegisterRequest: LessonRegisterRequest) = flow {
        emit(Result.Loading)
        val response =
            service.registerLesson(preferences.getAccessToken(), lessonRegisterRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonRegisterResponse.EMPTY))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun deleteLesson(lessonId: String) = flow {
        emit(Result.Loading)
        val response = service.deleteLesson(preferences.getAccessToken(), lessonId) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonDeleteResponse.EMPTY))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun fetchLessonCategoryList() = flow {
        emit(Result.Loading)
        val response = service.fetchLessonCategoryList(preferences.getAccessToken()) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonCategoryResponse.EMPTY)))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun fetchLessonSiteList() = flow {
        emit(Result.Loading)
        val response = service.fetchLessonSiteList(preferences.getAccessToken()) ?:run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonSiteResponse.EMPTY)))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun updateLesson(
        lessonId: String,
        lessonUpdateRequest: LessonUpdateRequest
    ) = flow {
        emit(Result.Loading)
        val response =
            service.updateLesson(preferences.getAccessToken(), lessonId, lessonUpdateRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferences.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonUpdateResponse.EMPTY))
            }
            401 -> {
                preferences.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }
}