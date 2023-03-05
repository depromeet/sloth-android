package com.depromeet.sloth.presentation.ui.todaylesson

import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse

sealed class TodayLessonUiModel {

    object TodayLessonEmptyItem : TodayLessonUiModel()

    data class TodayLessonHeaderItem(val itemType: TodayLessonType) : TodayLessonUiModel()

    data class TodayLessonTitleItem(val itemType: TodayLessonType) : TodayLessonUiModel()

    data class TodayLessonDoingItem(val todayLesson: TodayLessonResponse) : TodayLessonUiModel()

    data class TodayLessonFinishedItem(val todayLesson: TodayLessonResponse) : TodayLessonUiModel()
}
