package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonSiteEntity
import com.depromeet.presentation.model.LessonSite

internal fun LessonSite.toEntity() = LessonSiteEntity(
    siteId = siteId,
    siteName = siteName
)

internal fun LessonSiteEntity.toUiModel() = LessonSite(
    siteId = siteId,
    siteName = siteName
)