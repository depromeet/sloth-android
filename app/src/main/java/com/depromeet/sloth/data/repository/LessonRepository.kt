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

    fun fetchLessonDetail(lessonId: String): Flow<Result<LessonDetailResponse>>

    fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): Flow<Result<LessonRegisterResponse>>

    fun deleteLesson(lessonId: String): Flow<Result<LessonDeleteResponse>>

    fun fetchLessonCategoryList(): Flow<Result<List<LessonCategoryResponse>>>

    fun fetchLessonSiteList(): Flow<Result<List<LessonSiteResponse>>>

    fun updateLesson(
        lessonId: String,
        lessonUpdateRequest: LessonUpdateRequest
    ): Flow<Result<LessonUpdateResponse>>
}