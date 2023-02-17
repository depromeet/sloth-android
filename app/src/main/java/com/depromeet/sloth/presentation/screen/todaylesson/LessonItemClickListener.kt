package com.depromeet.sloth.presentation.screen.todaylesson

import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse

data class LessonItemClickListener(
    val onClick: () -> Unit,
    val onPlusClick: (TodayLessonResponse) -> Unit,
    val onMinusClick: (TodayLessonResponse) -> Unit,
    val onFinishClick: (TodayLessonResponse) -> Unit
)
