package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.LessonCategory
import com.depromeet.sloth.data.network.lesson.LessonSite
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonFinishResponse
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountResponse
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.ui.common.Result
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<Result<List<LessonTodayResponse>>>

    fun fetchAllLessonList(): Flow<Result<List<LessonAllResponse>>>

    fun finishLesson(lessonId: String): Flow<Result<LessonFinishResponse>>

    suspend fun updateLessonCount(count: Int, lessonId: Int): Result<LessonUpdateCountResponse>

    suspend fun fetchLessonDetail(lessonId: String): Result<LessonDetail>

    suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): Result<LessonRegisterResponse>

    // suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): LessonRegisterResponse?

    suspend fun deleteLesson(lessonId: String): Result<LessonDeleteResponse>

    suspend fun fetchLessonCategoryList(): Result<List<LessonCategory>>

    suspend fun fetchLessonSiteList(): Result<List<LessonSite>>

    suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest
    ): Result<LessonUpdateResponse>
}