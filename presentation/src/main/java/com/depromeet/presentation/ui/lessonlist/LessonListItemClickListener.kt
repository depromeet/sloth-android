package com.depromeet.presentation.ui.lessonlist

import com.depromeet.presentation.model.LessonList


data class LessonListItemClickListener(
    val onRegisterClick: () -> Unit,
    val onLessonClick:(LessonList) -> Unit
)
