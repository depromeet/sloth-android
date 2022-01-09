package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import javax.inject.Inject

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */
class LessonRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    suspend fun deleteLesson(
        accessToken: String,
        lessonId: String
    ): LessonState<LessonDeleteResponse> {
        RetrofitServiceGenerator.build(accessToken)
            .create(LessonService::class.java)
            .deleteLesson(lessonId)?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonDeleteResponse())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .deleteLesson(lessonId)?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(body() ?: LessonDeleteResponse())
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

    suspend fun fetchTodayLessonList(
        accessToken: String
    ): LessonState<List<LessonTodayResponse>> {
        RetrofitServiceGenerator.build(accessToken)
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

    suspend fun fetchAllLessonList(
        accessToken: String
    ): LessonState<List<LessonAllResponse>> {
        RetrofitServiceGenerator.build(accessToken)
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

    suspend fun updateLessonCount(
        accessToken: String,
        count: Int,
        lessonId: Int
    ): LessonState<LessonUpdateCountResponse> {
        RetrofitServiceGenerator.build(accessToken)
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
        accessToken: String,
        lessonId: String
    ): LessonState<LessonDetailResponse> {
        RetrofitServiceGenerator.build(accessToken)
            .create(LessonService::class.java)
            .fetchLessonDetail(lessonId)?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonDetailResponse())
                    401 -> {
                        val refreshToken = preferenceManager.getRefreshToken()
                        RetrofitServiceGenerator.build(refreshToken)
                            .create(LessonService::class.java)
                            .fetchLessonDetail(lessonId)?.run {
                                return when (code()) {
                                    200 -> {
                                        val newAccessToken = headers()["Authorization"] ?: "EMPTY"
                                        preferenceManager.updateAccessToken(newAccessToken)
                                        LessonState.Success(body() ?: LessonDetailResponse())
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

    suspend fun registerLesson(
        accessToken: String,
        request: LessonRegisterRequest
    ): LessonState<LessonRegisterResponse> {
        RetrofitServiceGenerator.build(accessToken)
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

    suspend fun fetchLessonCategoryList(
        accessToken: String,
    ): LessonState<List<LessonCategoryResponse>> {
        RetrofitServiceGenerator.build(accessToken)
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

    suspend fun fetchLessonSiteList(
        accessToken: String,
    ): LessonState<List<LessonSiteResponse>> {
        RetrofitServiceGenerator.build(accessToken)
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
        accessToken: String,
        lessonId: String,
        updateLessonRequest: LessonUpdateInfoRequest
    ): LessonUpdateState<LessonUpdateInfoResponse> {
        RetrofitServiceGenerator.build(accessToken)
            .create(LessonService::class.java)
            .updateLesson(lessonId, updateLessonRequest)?.run {
                return when (this.code()) {
                    200 -> LessonUpdateState.Success(this.body() ?: LessonUpdateInfoResponse())
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
                                        LessonUpdateState.Success(body() ?: LessonUpdateInfoResponse())
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