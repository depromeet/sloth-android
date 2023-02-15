package com.depromeet.sloth.presentation.screen.lessonlist

import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.presentation.adapter.HeaderAdapter

sealed class LessonUiModel {
    object EmptyLesson : LessonUiModel()
    data class LessonHeader(val headerType: HeaderAdapter.HeaderType, val count: Int?) :
        LessonUiModel()

    data class CurrentLesson(val lessonListResponse: LessonListResponse) : LessonUiModel()
    data class PlanLesson(val lessonListResponse: LessonListResponse) : LessonUiModel()
    data class PastLesson(val lessonListResponse: LessonListResponse) : LessonUiModel()
}
