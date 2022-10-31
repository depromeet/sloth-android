package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.network.lesson.LessonCategory
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.LessonSite
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.ui.base.UiState
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<UiState<List<LessonTodayResponse>>>

    fun fetchAllLessonList(): Flow<UiState<List<LessonAllResponse>>>

    fun finishLesson(lessonId: String): Flow<UiState<LessonFinishResponse>>

    suspend fun updateLessonCount(count: Int, lessonId: Int): LessonState<LessonUpdateCountResponse>

    suspend fun fetchLessonDetail(lessonId: String): LessonState<LessonDetail>

    suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): LessonState<LessonRegisterResponse>

    suspend fun deleteLesson(lessonId: String): LessonState<LessonDeleteResponse>

    suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategory>>

    //fun fetchLessonCategoryList(): Flow<UiState<List<LessonCategory>>>

    suspend fun fetchLessonSiteList(): LessonState<List<LessonSite>>

    //fun fetchLessonSiteList(): Flow<UiState<List<LessonSite>>>

    suspend fun updateLesson(lessonId: String, updateLessonRequest: LessonUpdateRequest): LessonState<LessonUpdateResponse>
}