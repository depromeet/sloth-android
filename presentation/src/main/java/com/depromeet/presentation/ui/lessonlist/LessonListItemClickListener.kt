package com.depromeet.presentation.ui.lessonlist

import com.depromeet.presentation.model.Lesson


data class LessonListItemClickListener(
    val onRegisterClick: () -> Unit,
    val onLessonClick:(Lesson) -> Unit
)
