package com.depromeet.presentation.ui.todaylesson

import com.depromeet.presentation.model.TodayLesson


sealed class TodayLessonUiModel {

    object TodayLessonEmptyItem : TodayLessonUiModel()

    data class TodayLessonHeaderItem(val itemType: TodayLessonType) : TodayLessonUiModel()

    data class TodayLessonTitleItem(val itemType: TodayLessonType) : TodayLessonUiModel()

    data class TodayLessonDoingItem(val todayLesson: TodayLesson) : TodayLessonUiModel()

    data class TodayLessonFinishedItem(val todayLesson: TodayLesson) : TodayLessonUiModel()
}
