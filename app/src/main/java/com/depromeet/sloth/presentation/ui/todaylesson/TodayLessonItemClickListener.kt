package com.depromeet.sloth.presentation.ui.todaylesson

import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse

data class TodayLessonItemClickListener(
    val onClick: () -> Unit,
    val onPlusClick: (TodayLessonResponse) -> Unit,
    val onMinusClick: (TodayLessonResponse) -> Unit,
    val onFinishClick: (TodayLessonResponse) -> Unit
)