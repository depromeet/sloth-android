package com.depromeet.data.mapper

import com.depromeet.data.model.response.lesson.LessonSiteResponse
import com.depromeet.domain.entity.LessonSiteEntity

internal fun List<LessonSiteResponse>.toEntity(): List<LessonSiteEntity> = map {
    LessonSiteEntity(
        siteId = it.siteId,
        siteName = it.siteName
    )
}