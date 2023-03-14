package com.depromeet.domain.repository

import com.depromeet.domain.entity.*
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface LessonRepository {

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