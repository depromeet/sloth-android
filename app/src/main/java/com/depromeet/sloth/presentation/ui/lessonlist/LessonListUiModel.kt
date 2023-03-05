package com.depromeet.sloth.presentation.ui.lessonlist

import com.depromeet.sloth.data.model.response.lesson.LessonListResponse

sealed class LessonListUiModel {
    object LessonListEmptyItem : LessonListUiModel()

    // data class LessonListTitleItem(@StringRes val commentRes: Int, val count: Int?) : LessonListUiModel()
    data class LessonListTitleItem(val itemType: LessonListType, val count: Int?) : LessonListUiModel()

    data class LessonListCurrentItem(val lessonList: LessonListResponse) : LessonListUiModel()

    data class LessonListPlanItem(val lessonList: LessonListResponse) : LessonListUiModel()

    data class LessonListPastItem(val lessonList: LessonListResponse) : LessonListUiModel()
}
