package com.depromeet.sloth.presentation.ui.lessonlist

import com.depromeet.sloth.data.model.response.lesson.LessonListResponse

data class LessonListItemClickListener(
    val onRegisterClick: () -> Unit,
    val onLessonClick:(LessonListResponse) -> Unit
)
