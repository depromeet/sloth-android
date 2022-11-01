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
import com.depromeet.sloth.ui.common.UiState
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<UiState<List<LessonTodayResponse>>>

    fun fetchAllLessonList(): Flow<UiState<List<LessonAllResponse>>>

    fun finishLesson(lessonId: String): Flow<UiState<LessonFinishResponse>>

    suspend fun updateLessonCount(count: Int, lessonId: Int): UiState<LessonUpdateCountResponse>

    suspend fun fetchLessonDetail(lessonId: String): UiState<LessonDetail>

    suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): UiState<LessonRegisterResponse>

    // suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): LessonRegisterResponse?

    suspend fun deleteLesson(lessonId: String): UiState<LessonDeleteResponse>

    suspend fun fetchLessonCategoryList(): UiState<List<LessonCategory>>

    //fun fetchLessonCategoryList(): Flow<UiState<List<LessonCategory>>>

    suspend fun fetchLessonSiteList(): UiState<List<LessonSite>>

    //fun fetchLessonSiteList(): Flow<UiState<List<LessonSite>>>

    suspend fun updateLesson(lessonId: String, updateLessonRequest: LessonUpdateRequest): UiState<LessonUpdateResponse>
}