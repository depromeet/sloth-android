package com.depromeet.data.source.remote

import com.depromeet.domain.entity.LessonCategoryEntity
import com.depromeet.domain.entity.LessonDeleteEntity
import com.depromeet.domain.entity.LessonDetailEntity
import com.depromeet.domain.entity.LessonEntity
import com.depromeet.domain.entity.LessonFinishEntity
import com.depromeet.domain.entity.LessonRegisterEntity
import com.depromeet.domain.entity.LessonRegisterRequestEntity
import com.depromeet.domain.entity.LessonSiteEntity
import com.depromeet.domain.entity.LessonStatisticsEntity
import com.depromeet.domain.entity.LessonUpdateEntity
import com.depromeet.domain.entity.LessonUpdateRequestEntity
import com.depromeet.domain.entity.TodayLessonEntity
import com.depromeet.domain.entity.UpdateLessonCountEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface LessonRemoteDataSource {

    fun fetchTodayLessonList(): Flow<Result<List<TodayLessonEntity>>>

    fun fetchLessonList(): Flow<Result<List<LessonEntity>>>

    fun finishLesson(lessonId: String): Flow<Result<LessonFinishEntity>>

    fun updateLessonCount(count: Int, lessonId: Int): Flow<Result<UpdateLessonCountEntity>>

    fun fetchLessonDetail(lessonId: String): Flow<Result<LessonDetailEntity>>

    fun registerLesson(lessonRegisterRequestEntity: LessonRegisterRequestEntity): Flow<Result<LessonRegisterEntity>>

    fun deleteLesson(lessonId: String): Flow<Result<LessonDeleteEntity>>

    fun fetchLessonCategoryList(): Flow<Result<List<LessonCategoryEntity>>>

    fun fetchLessonSiteList(): Flow<Result<List<LessonSiteEntity>>>

    fun updateLesson(
        lessonId: String,
        lessonUpdateRequestEntity: LessonUpdateRequestEntity
    ): Flow<Result<LessonUpdateEntity>>

    fun fetchLessonStatisticsInformation(): Flow<Result<LessonStatisticsEntity>>
}