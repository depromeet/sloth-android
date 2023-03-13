package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonUpdateEntity
import com.depromeet.presentation.model.LessonUpdate

internal fun LessonUpdate.toEntity() = LessonUpdateEntity(
    categoryId = categoryId,
    lessonId = lessonId,
    lessonName = lessonName,
    siteId = siteId,
    totalNumber = totalNumber
)

internal fun LessonUpdateEntity.toUiModel() = LessonUpdate(
    categoryId = categoryId,
    lessonId = lessonId,
    lessonName = lessonName,
    siteId = siteId,
    totalNumber = totalNumber
)