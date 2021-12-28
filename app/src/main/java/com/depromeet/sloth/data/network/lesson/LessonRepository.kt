package com.depromeet.sloth.data.network.lesson

import android.os.Build
import androidx.annotation.RequiresApi
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.RetrofitServiceGenerator

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */
class LessonRepository {
    suspend fun deleteLesson(
        accessToken: String,
        lessonId: String
    ): LessonState<LessonDeleteResponse> {
        RetrofitServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .deleteLesson(
                lessonId,
            )?.run {
                return when(this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonDeleteResponse())
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchTodayLessonList(
        accessToken: String
    ): LessonState<List<LessonTodayResponse>> {
        RetrofitServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchTodayLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf(LessonTodayResponse.EMPTY))
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchAllLessonList(
        accessToken: String
    ): LessonState<List<LessonAllResponse>> {
        RetrofitServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchAllLessonList()?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: listOf())
                    401 -> LessonState.Unauthorized
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
        RetrofitServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .updateLessonCount(
                LessonUpdateCountRequest(
                    count = count,
                    lessonId = lessonId
                )
            )?.run {
                return when (this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchLessonDetail(
        accessToken: String,
        lessonId: String
    ): LessonState<LessonDetailResponse> {
        RetrofitServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .fetchLessonDetail(
                lessonId,
            )?.run {
                return when(this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonDetailResponse())
                    401 -> LessonState.Unauthorized
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
        RetrofitServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .registerLesson(
                LessonRegisterRequest(
                    alertDays = request.alertDays,
                    categoryId = request.categoryId,
                    endDate = request.endDate,
                    lessonName = request.lessonName,
                    message = request.message,
                    price = request.price,
                    siteId = request.siteId,
                    startDate = request.startDate,
                    totalNumber = request.totalNumber
                )
            )?.run {
                return when(this.code()) {
                    200 -> LessonState.Success(this.body() ?: LessonRegisterResponse())
                    401 -> LessonState.Unauthorized
                    403 -> LessonState.Forbidden
                    404 -> LessonState.NotFound
                    else -> LessonState.Error(java.lang.Exception("Uncaught Exception"))
                }
            } ?: return LessonState.Error(Exception("Register Exception"))
    }

    suspend fun updateLesson(
        accessToken: String,
        lessonId: String,
        updateLessonRequest: LessonUpdateInfoRequest
    ): LessonUpdateState<LessonUpdateInfoResponse> {
        RetrofitServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        )
            .create(LessonService::class.java)
            .updateLesson(
                lessonId,
                LessonUpdateInfoRequest(
                    categoryId = updateLessonRequest.categoryId,
                    lessonName = updateLessonRequest.lessonName,
                    siteId = updateLessonRequest.siteId,
                    totalNumber = updateLessonRequest.totalNumber
                )
            )?.run {
                return when(this.code()) {
                    200 -> LessonUpdateState.Success(this.body() ?: LessonUpdateInfoResponse())
                    204 -> LessonUpdateState.NoContent
                    401 -> LessonUpdateState.Unauthorized
                    403 -> LessonUpdateState.Forbidden
                    else -> LessonUpdateState.Error(Exception("Uncaught Exception"))
                }
            } ?: return LessonUpdateState.Error(Exception("Retrofit Exception"))
    }
}