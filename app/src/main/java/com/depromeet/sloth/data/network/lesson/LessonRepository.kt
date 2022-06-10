package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.lesson.list.LessonAllResponse
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.data.network.lesson.list.LessonUpdateCountResponse
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import com.depromeet.sloth.ui.base.UIState
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun fetchTodayLessonList(): Flow<UIState<List<LessonTodayResponse>>>

    fun fetchAllLessonList(): Flow<UIState<List<LessonAllResponse>>>

    suspend fun updateLessonCount(count: Int, lessonId: Int): LessonState<LessonUpdateCountResponse>

    suspend fun fetchLessonDetail(lessonId: String): LessonDetailState<LessonDetail>

    suspend fun registerLesson(request: LessonRegisterRequest): LessonState<LessonRegisterResponse>

    suspend fun deleteLesson(lessonId: String): LessonDeleteState<LessonDeleteResponse>

    suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategoryResponse>>

    suspend fun fetchLessonSiteList(): LessonState<List<LessonSiteResponse>>

    suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest,
    ): LessonUpdateState<LessonUpdateResponse>
}