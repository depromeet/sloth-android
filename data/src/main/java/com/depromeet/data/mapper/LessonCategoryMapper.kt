package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.domain.entity.LessonCategoryEntity

internal fun List<LessonCategoryResponse>.toEntity(): List<LessonCategoryEntity> = map {
    LessonCategoryEntity(
        categoryId = it.categoryId,
        categoryName = it.categoryName
    )
}