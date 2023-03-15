package com.depromeet.presentation.ui.lessonlist

import com.depromeet.presentation.model.Lesson


sealed class LessonListUiModel {
    object LessonListEmptyItem : LessonListUiModel()

    // data class LessonListTitleItem(@StringRes val commentRes: Int, val count: Int?) : LessonListUiModel()
    data class LessonListTitleItem(val itemType: LessonListType, val count: Int?) : LessonListUiModel()

    data class LessonListCurrentItem(val lesson: Lesson) : LessonListUiModel()

    data class LessonListPlanItem(val lesson: Lesson) : LessonListUiModel()

    data class LessonListPastItem(val lesson: Lesson) : LessonListUiModel()
}
