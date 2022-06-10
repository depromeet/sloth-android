package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountRequest
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountResponse
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import retrofit2.Response
import retrofit2.http.*

interface LessonService {
    @GET("api/lesson/doing")
    suspend fun fetchTodayLessonList(): Response<List<LessonTodayResponse>>?

    @GET("api/lesson/list")
    suspend fun fetchAllLessonList(): Response<List<LessonAllResponse>>?

    @PATCH("api/lesson/number")
    suspend fun updateLessonCount(@Body request: LessonUpdateCountRequest): Response<LessonUpdateCountResponse>?

    @GET("api/lesson/detail/{lessonId}")
    suspend fun fetchLessonDetail(@Path("lessonId") lessonId: String): Response<LessonDetail>?

    @DELETE("api/lesson/{lessonId}")
    suspend fun deleteLesson(@Path("lessonId") lessonId: String): Response<LessonDeleteResponse>?

    @POST("api/lesson")
    suspend fun registerLesson(@Body request: LessonRegisterRequest): Response<LessonRegisterResponse>?

    @PATCH("api/lesson/{lessonId}")
    suspend fun updateLesson(@Path("lessonId") lessonId: String, @Body request: LessonUpdateRequest): Response<LessonUpdateResponse>?

    @GET("api/category/list")
    suspend fun fetchLessonCategoryList(): Response<List<LessonCategoryResponse>>?

    @GET("api/site/list")
    suspend fun fetchLessonSiteList(): Response<List<LessonSiteResponse>>?
}