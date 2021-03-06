package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.model.LessonCategory
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.model.LessonSite
import com.depromeet.sloth.data.model.LessonUpdate
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.ui.base.UIState
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<UIState<List<LessonTodayResponse>>>

    fun fetchAllLessonList(): Flow<UIState<List<LessonAllResponse>>>

    fun finishLesson(lessonId: String): Flow<UIState<LessonFinishResponse>>

    suspend fun updateLessonCount(count: Int, lessonId: Int): LessonState<LessonUpdateCountResponse>

    suspend fun fetchLessonDetail(lessonId: String): LessonState<LessonDetail>

    suspend fun registerLesson(lessonRegisterRequest: LessonRegisterRequest): LessonState<LessonRegisterResponse>

    suspend fun deleteLesson(lessonId: String): LessonState<LessonDeleteResponse>

    suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategory>>

    //fun fetchLessonCategoryList(): Flow<UIState<List<LessonCategory>>>

    suspend fun fetchLessonSiteList(): LessonState<List<LessonSite>>

    suspend fun updateLesson(lessonId: String, updateLessonRequest: LessonUpdateRequest): LessonState<LessonUpdate>
}