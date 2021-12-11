package com.depromeet.sloth.data.network.update


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UpdateLessonService {

    @PATCH("api/lesson/{lessonId}")
    suspend fun updateLessonInfo(@Path("lessonId") lessonId: String, @Body request: UpdateLessonRequest): Response<UpdateLessonResponse>?
}