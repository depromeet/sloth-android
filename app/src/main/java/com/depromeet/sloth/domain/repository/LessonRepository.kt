package com.depromeet.sloth.domain.repository

import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.*
import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.data.model.response.lesson.UpdateLessonCountResponse
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<Result<List<TodayLessonResponse>>>

    fun fetchLessonList(): Flow<Result<List<LessonListResponse>>>

    fun finishLesson(lessonId: String): Flow<Result<LessonFinishResponse>>

    fun updateLessonCount(count: Int, lessonId: Int): Flow<Result<UpdateLessonCountResponse>>

    fun fetchLessonDetail(lessonId: String): Flow<Result<LessonDetailResponse>>

    fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): Flow<Result<LessonRegisterResponse>>

    fun deleteLesson(lessonId: String): Flow<Result<LessonDeleteResponse>>

    fun fetchLessonCategoryList(): Flow<Result<List<LessonCategoryResponse>>>

    fun fetchLessonSiteList(): Flow<Result<List<LessonSiteResponse>>>

    fun updateLesson(
        lessonId: String,
        lessonUpdateRequest: LessonUpdateRequest
    ): Flow<Result<LessonUpdateResponse>>

    fun fetchLessonStatisticsInformation(): Flow<Result<LessonStatisticsResponse>>
}