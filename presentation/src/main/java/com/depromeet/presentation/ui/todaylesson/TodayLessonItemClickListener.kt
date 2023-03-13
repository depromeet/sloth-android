package com.depromeet.presentation.ui.todaylesson

import com.depromeet.presentation.model.TodayLesson


data class TodayLessonItemClickListener(
    val onClick: () -> Unit,
    val onPlusClick: (TodayLesson) -> Unit,
    val onMinusClick: (TodayLesson) -> Unit,
    val onFinishClick: (TodayLesson) -> Unit
)
