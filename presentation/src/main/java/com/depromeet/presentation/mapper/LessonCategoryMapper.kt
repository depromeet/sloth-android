package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonCategoryEntity
import com.depromeet.presentation.model.LessonCategory

internal fun LessonCategory.toEntity() = LessonCategoryEntity(
    categoryId = categoryId,
    categoryName = categoryName
)

internal fun LessonCategoryEntity.toUiModel() = LessonCategory(
    categoryId = categoryId,
    categoryName = categoryName
)