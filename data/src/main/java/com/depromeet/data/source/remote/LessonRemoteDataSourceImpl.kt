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
import com.depromeet.data.util.RESPONSE_NULL_ERROR
import com.depromeet.data.util.handleExceptions
import com.depromeet.data.util.handleResponse
import com.depromeet.domain.entity.LessonCategoryEntity
import com.depromeet.domain.entity.LessonEntity
import com.depromeet.domain.entity.LessonRegisterRequestEntity
import com.depromeet.domain.entity.LessonSiteEntity
import com.depromeet.domain.entity.LessonUpdateRequestEntity
import com.depromeet.domain.entity.TodayLessonEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class LessonRemoteDataSourceImpl @Inject constructor(
    private val lessonService: LessonService,
    private val preferences: PreferenceManager
) : LessonRemoteDataSource {
    override fun fetchTodayLessonList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchTodayLessonList() ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: emptyList<TodayLessonEntity>()
        })
    }.handleExceptions()

    override fun fetchLessonList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonList() ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: emptyList<LessonEntity>()
        })
    }.handleExceptions()

    override fun finishLesson(lessonId: String) = flow {
        emit(Result.Loading)
        val response = lessonService.finishLesson(lessonId) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: LessonFinishResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override fun updateLessonCount(count: Int, lessonId: Int) = flow {
        emit(Result.Loading)
        val response = lessonService.updateLessonCount(LessonUpdateCountRequest(count, lessonId)) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: UpdateLessonCountResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override fun fetchLessonDetail(lessonId: String) = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonDetail(lessonId) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: LessonDetailResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override fun registerLesson(lessonRegisterRequestEntity: LessonRegisterRequestEntity) = flow {
        emit(Result.Loading)
        val response = lessonService.registerLesson(lessonRegisterRequestEntity.toModel())
            ?: run {
                emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                return@flow
            }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: LessonRegisterResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override fun deleteLesson(lessonId: String) = flow {
        emit(Result.Loading)
        val response = lessonService.deleteLesson(lessonId) ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: LessonDeleteResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override fun fetchLessonCategoryList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonCategoryList() ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: emptyList<LessonCategoryEntity>()
        })
    }.handleExceptions()

    override fun fetchLessonSiteList() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonSiteList() ?: run {
            emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
            return@flow
        }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: emptyList<LessonSiteEntity>()
        })
    }.handleExceptions()

    override fun updateLesson(lessonId: String, lessonUpdateRequestEntity: LessonUpdateRequestEntity) = flow {
        emit(Result.Loading)
        val response = lessonService.updateLesson(lessonId, lessonUpdateRequestEntity.toModel())
            ?: run {
                emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                return@flow
            }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: LessonUpdateResponse.EMPTY.toEntity()
        })
    }.handleExceptions()

    override fun fetchLessonStatisticsInformation() = flow {
        emit(Result.Loading)
        val response = lessonService.fetchLessonStatisticsInformation()
            ?: run {
                emit(Result.Error(Exception(RESPONSE_NULL_ERROR)))
                return@flow
            }
        emit(response.handleResponse(preferences) {
            it.body()?.toEntity() ?: LessonStatisticsResponse.EMPTY.toEntity()
        })
    }.handleExceptions()
}