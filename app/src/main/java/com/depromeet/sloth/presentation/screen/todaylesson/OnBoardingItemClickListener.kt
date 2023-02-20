package com.depromeet.sloth.presentation.screen.todaylesson

import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse

data class OnBoardingItemClickListener(
    val onPlusClick: (TodayLessonResponse) -> Unit,
    val onMinusClick: (TodayLessonResponse) -> Unit,
    val onFinishClick: () -> Unit
)
