package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.model.*
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import com.depromeet.sloth.ui.base.UIState
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<UIState<List<LessonTodayResponse>>>

    fun fetchAllLessonList(): Flow<UIState<List<LessonAllResponse>>>

    fun finishLesson(lessonId: String): Flow<UIState<LessonFinishResponse>>

    suspend fun updateLessonCount(count: Int, lessonId: Int): LessonState<LessonUpdateCountResponse>

    suspend fun fetchLessonDetail(lessonId: String): LessonDetailState<LessonDetail>

    suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): LessonState<LessonRegisterResponse>

    suspend fun deleteLesson(lessonId: String): LessonDeleteState<LessonDeleteResponse>

    suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategory>>

    //fun fetchLessonCategoryList(): Flow<UIState<List<LessonCategory>>>

    suspend fun fetchLessonSiteList(): LessonState<List<LessonSite>>

    suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest,
    ): LessonUpdateState<LessonUpdate>
}