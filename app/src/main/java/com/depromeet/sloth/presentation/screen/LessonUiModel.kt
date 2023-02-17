package com.depromeet.sloth.presentation.screen

import com.depromeet.sloth.data.model.response.lesson.LessonListResponse
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import com.depromeet.sloth.presentation.adapter.HeaderAdapter

sealed class LessonUiModel {
    object EmptyLesson : LessonUiModel()
    data class LessonHeader(val headerType: HeaderAdapter.HeaderType, val count: Int?) :
        LessonUiModel()
    data class OnBoardingDoingLesson(val todayLesson: TodayLessonResponse) : LessonUiModel()
    data class OnBoardingFinishedLesson(val todayLesson: TodayLessonResponse) : LessonUiModel()
    data class DoingLesson(val todayLesson: TodayLessonResponse) : LessonUiModel()
    data class FinishedLesson(val todayLesson: TodayLessonResponse) : LessonUiModel()
    data class CurrentLesson(val lessonList: LessonListResponse) : LessonUiModel()
    data class PlanLesson(val lessonList: LessonListResponse) : LessonUiModel()
    data class PastLesson(val lessonList: LessonListResponse) : LessonUiModel()
}
