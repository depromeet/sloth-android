package com.depromeet.presentation.ui.lessonlist

import com.depromeet.presentation.model.LessonList


sealed class LessonListUiModel {
    object LessonListEmptyItem : LessonListUiModel()

    // data class LessonListTitleItem(@StringRes val commentRes: Int, val count: Int?) : LessonListUiModel()
    data class LessonListTitleItem(val itemType: LessonListType, val count: Int?) : LessonListUiModel()

    data class LessonListCurrentItem(val lessonList: LessonList) : LessonListUiModel()

    data class LessonListPlanItem(val lessonList: LessonList) : LessonListUiModel()

    data class LessonListPastItem(val lessonList: LessonList) : LessonListUiModel()
}
