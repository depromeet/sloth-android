package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.domain.entity.LessonCategoryEntity

internal fun LessonCategoryResponse.toEntity() = LessonCategoryEntity(
    categoryId = categoryId,
    categoryName = categoryName
)

internal fun LessonCategoryEntity.toModel() = LessonCategoryResponse(
    categoryId = categoryId,
    categoryName = categoryName
)