package com.depromeet.sloth.data.network.service

import com.depromeet.sloth.data.model.*
import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateCountRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.*
import retrofit2.Response
import retrofit2.http.*

interface LessonService {
    @GET("api/lesson/doing")
    suspend fun fetchTodayLessonList(): Response<List<LessonTodayResponse>>?

    @GET("api/lesson/list")
    suspend fun fetchLessonList(): Response<List<LessonListResponse>>?

    @PATCH("api/lesson/number")
    suspend fun updateLessonCount(
        @Body lessonUpdateCountRequest: LessonUpdateCountRequest
    ): Response<LessonUpdateCountResponse>?

    @GET("api/lesson/detail/{lessonId}")
    suspend fun fetchLessonDetail(
        @Path("lessonId") lessonId: String
    ): Response<LessonDetailResponse>?

    @DELETE("api/lesson/{lessonId}")
    suspend fun deleteLesson(
        @Path("lessonId") lessonId: String
    ): Response<LessonDeleteResponse>?

    @POST("api/lesson")
    suspend fun registerLesson(
        @Body lessonRegisterRequest: LessonRegisterRequest
    ): Response<LessonRegisterResponse>?

    @PATCH("api/lesson/{lessonId}")
    suspend fun updateLesson(
        @Path("lessonId") lessonId: String,
        @Body lessonUpdateRequest: LessonUpdateRequest
    ): Response<LessonUpdateResponse>?

    @PATCH("api/lesson/{lessonId}/finish")
    suspend fun finishLesson(
        @Path("lessonId") lessonId: String
    ): Response<LessonFinishResponse>?

    @GET("api/category/list")
    suspend fun fetchLessonCategoryList(): Response<List<LessonCategoryResponse>>?

    @GET("api/site/list")
    suspend fun fetchLessonSiteList(): Response<List<LessonSiteResponse>>?
}