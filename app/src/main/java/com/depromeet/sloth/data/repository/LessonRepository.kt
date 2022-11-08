package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.sloth.data.model.response.lesson.LessonSiteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.data.model.response.lesson.LessonFinishResponse
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.response.lesson.LessonRegisterResponse
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.sloth.common.Result
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<Result<List<LessonTodayResponse>>>

    fun fetchAllLessonList(): Flow<Result<List<LessonAllResponse>>>

    fun finishLesson(lessonId: String): Flow<Result<LessonFinishResponse>>

    suspend fun updateLessonCount(count: Int, lessonId: Int): Result<LessonUpdateCountResponse>

//    suspend fun fetchLessonDetail(lessonId: String): Result<LessonDetailResponse>

    fun fetchLessonDetail(lessonId: String): Flow<Result<LessonDetailResponse>>

//    suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): Result<LessonRegisterResponse>

//    suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): LessonRegisterResponse?

    fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): Flow<Result<LessonRegisterResponse>>

//    suspend fun deleteLesson(lessonId: String): Result<LessonDeleteResponse>

    fun deleteLesson(lessonId: String): Flow<Result<LessonDeleteResponse>>

    suspend fun fetchLessonCategoryList(): Result<List<LessonCategoryResponse>>

//    fun fetchLessonCategoryList(): Flow<Result<LessonCategoryResponse>>

    suspend fun fetchLessonSiteList(): Result<List<LessonSiteResponse>>

//    fun fetchLessonSiteList(): Flow<Result<LessonSiteResponse>>

//    suspend fun updateLesson(
//        lessonId: String,
//        updateLessonRequest: LessonUpdateRequest
//    ): Result<LessonUpdateResponse>

    fun updateLesson(
        lessonId: String,
        lessonUpdateRequest: LessonUpdateRequest
    ): Flow<Result<LessonUpdateResponse>>

//    fun updateLesson(
//        lessonId: String,
//        lessonUpdateRequest: LessonUpdateRequest
//    ): Flow<LessonUpdateResponse>
}