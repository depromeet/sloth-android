package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonCategoryEntity
import com.depromeet.presentation.model.LessonCategory

internal fun List<LessonCategoryEntity>.toUiModel(): List<LessonCategory> = map {
    LessonCategory(
        categoryId = it.categoryId,
        categoryName = it.categoryName
    )
}
