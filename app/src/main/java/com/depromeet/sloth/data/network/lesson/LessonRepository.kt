package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import javax.inject.Inject

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */
class LessonRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    suspend fun deleteLesson(lessonId: String): LessonDeleteState<LessonDeleteResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .deleteLesson(lessonId)?.run {
                return when (this.code()) {
                    200 -> LessonDeleteState.Success(this.body() ?: LessonDeleteResponse())
                    204 -> LessonDeleteState.NoContent
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .deleteLesson(lessonId)?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonDeleteState.Success(body() ?: LessonDeleteResponse())
                                    }
                                    else -> LessonDeleteState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: LessonDeleteState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonDeleteState.Forbidden
                    else -> LessonDeleteState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonDeleteState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchTodayLessonList(): LessonState<List<LessonTodayResponse>> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchTodayLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(body() ?: listOf(LessonTodayResponse.EMPTY))
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .fetchTodayLessonList()?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(
                                            body() ?: listOf(LessonTodayResponse.EMPTY)
                                        )
                                    }
                                    else -> LessonState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: LessonState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchAllLessonList(): LessonState<List<LessonAllResponse>> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchAllLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf(LessonAllResponse.EMPTY))
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .fetchAllLessonList()?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(
                                            body() ?: listOf(LessonAllResponse.EMPTY)
                                        )
                                    }
                                    else -> LessonState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: LessonState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateLessonCount(count: Int, lessonId: Int): LessonState<LessonUpdateCountResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .updateLessonCount(LessonUpdateCountRequest(count, lessonId))?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .updateLessonCount(LessonUpdateCountRequest(count, lessonId))?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(
                                            body() ?: LessonUpdateCountResponse.EMPTY
                                        )
                                    }
                                    else -> LessonState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: LessonState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchLessonDetail(
        lessonId: String
    ): LessonDetailState<LessonDetailResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonDetail(lessonId)?.run {
                return when (this.code()) {
                    200 ->  LessonDetailState.Success(this.body() ?: LessonDetailResponse())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .fetchLessonDetail(lessonId)?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonDetailState.Success(body() ?: LessonDetailResponse())
                                    }
                                    else ->  LessonDetailState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?:  LessonDetailState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonDetailState.Forbidden
                    404 ->  LessonDetailState.NotFound
                    else ->  LessonDetailState.Error(Exception("Uncaught Exception"))
                }
            } ?: return  LessonDetailState.Error(Exception("Retrofit Exception"))
    }

    suspend fun registerLesson(
        request: LessonRegisterRequest
    ): LessonState<LessonRegisterResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .registerLesson(request)?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonRegisterResponse())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .registerLesson(request)?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(body() ?: LessonRegisterResponse())
                                    }
                                    else -> LessonState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: LessonState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(java.lang.Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Register Exception"))
    }

    suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategoryResponse>> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonCategoryList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .fetchLessonCategoryList()?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(body() ?: listOf())
                                    }
                                    else -> LessonState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: LessonState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchLessonSiteList(): LessonState<List<LessonSiteResponse>> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonSiteList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .fetchLessonSiteList()?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(body() ?: listOf())
                                    }
                                    else -> LessonState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?: LessonState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest
    ): LessonUpdateState<LessonUpdateResponse> {
        RetrofitServiceGenerator.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .updateLesson(lessonId, updateLessonRequest)?.run {
                return when (this.code()) {
                    200 -> LessonUpdateState.Success(this.body() ?: LessonUpdateResponse())
                    204 -> LessonUpdateState.NoContent
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .updateLesson(lessonId, updateLessonRequest)?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonUpdateState.Success(body() ?: LessonUpdateResponse())
                                    }
                                    else ->  LessonUpdateState.Unauthorized(Exception("Authentication Failed Exception"))
                                }
                            } ?:  LessonUpdateState.Error(Exception("Uncaught Exception"))
                    }
                    403 -> LessonUpdateState.Forbidden
                    else -> LessonUpdateState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonUpdateState.Error(Exception("Retrofit Exception"))
    }
}