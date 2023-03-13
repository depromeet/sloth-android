package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonSiteResponse
import com.depromeet.domain.entity.LessonSiteEntity

internal fun LessonSiteResponse.toEntity() = LessonSiteEntity(
    siteId = siteId,
    siteName = siteName
)

internal fun LessonSiteEntity.toModel() = LessonSiteResponse(
    siteId = siteId,
    siteName = siteName
)