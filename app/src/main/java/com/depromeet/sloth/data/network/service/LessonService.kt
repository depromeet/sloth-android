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
    suspend fun fetchTodayLessonList(@Header("Authorization") accessToken: String?): Response<List<LessonTodayResponse>>?

    @GET("api/lesson/list")
    suspend fun fetchAllLessonList(@Header("Authorization") accessToken: String?): Response<List<LessonAllResponse>>?

    @PATCH("api/lesson/number")
    suspend fun updateLessonCount(
        @Header("Authorization") accessToken: String?,
        @Body request: LessonUpdateCountRequest
    ): Response<LessonUpdateCountResponse>?

    @GET("api/lesson/detail/{lessonId}")
    suspend fun fetchLessonDetail(
        @Header("Authorization") accessToken: String?,
        @Path("lessonId") lessonId: String
    ): Response<LessonDetailResponse>?

    @DELETE("api/lesson/{lessonId}")
    suspend fun deleteLesson(
        @Header("Authorization") accessToken: String?,
        @Path("lessonId") lessonId: String
    ): Response<LessonDeleteResponse>?

    @POST("api/lesson")
    suspend fun registerLesson(
        @Header("Authorization") accessToken: String?,
        @Body lessonRegisterRequest: LessonRegisterRequest
    ): Response<LessonRegisterResponse>?

    @PATCH("api/lesson/{lessonId}")
    suspend fun updateLesson(
        @Header("Authorization") accessToken: String?,
        @Path("lessonId") lessonId: String,
        @Body lessonUpdateRequest: LessonUpdateRequest
    ): Response<LessonUpdateResponse>?

    @PATCH("api/lesson/{lessonId}/finish")
    suspend fun finishLesson(
        @Header("Authorization") accessToken: String?,
        @Path("lessonId") lessonId: String
    ): Response<LessonFinishResponse>?

    @GET("api/category/list")
    suspend fun fetchLessonCategoryList(
        @Header("Authorization") accessToken: String?
    ): Response<List<LessonCategoryResponse>>?

    @GET("api/site/list")
    suspend fun fetchLessonSiteList(
        @Header("Authorization") accessToken: String?
    ): Response<List<LessonSiteResponse>>?
}