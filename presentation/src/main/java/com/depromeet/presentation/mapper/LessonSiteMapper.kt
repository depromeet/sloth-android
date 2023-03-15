package com.depromeet.presentation.mapper

import com.depromeet.domain.entity.LessonSiteEntity
import com.depromeet.presentation.model.LessonSite


internal fun List<LessonSiteEntity>.toUiModel(): List<LessonSite> = map {
    LessonSite(
        siteId = it.siteId,
        siteName = it.siteName
    )
}