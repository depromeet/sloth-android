package com.depromeet.data.repository

import com.depromeet.data.source.remote.LessonRemoteDataSource
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
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LessonRepositoryImpl @Inject constructor(
    private val lessonRemoteDataSource: LessonRemoteDataSource
) : LessonRepository {

    override fun fetchTodayLessonList(): Flow<Result<List<TodayLessonEntity>>> {
        return lessonRemoteDataSource.fetchTodayLessonList()
    }

    override fun fetchLessonList(): Flow<Result<List<LessonEntity>>> {
        return lessonRemoteDataSource.fetchLessonList()
    }

    override fun finishLesson(lessonId: String): Flow<Result<LessonFinishEntity>> {
        return lessonRemoteDataSource.finishLesson(lessonId)
    }

    override fun updateLessonCount(count: Int, lessonId: Int): Flow<Result<UpdateLessonCountEntity>> {
        return lessonRemoteDataSource.updateLessonCount(count, lessonId)
    }

    override fun fetchLessonDetail(lessonId: String): Flow<Result<LessonDetailEntity>> {
        return lessonRemoteDataSource.fetchLessonDetail(lessonId)
    }

    override fun registerLesson(lessonRegisterRequestEntity: LessonRegisterRequestEntity): Flow<Result<LessonRegisterEntity>> {
        return lessonRemoteDataSource.registerLesson(lessonRegisterRequestEntity)
    }

    override fun deleteLesson(lessonId: String): Flow<Result<LessonDeleteEntity>> {
        return lessonRemoteDataSource.deleteLesson(lessonId)
    }

    override fun fetchLessonCategoryList(): Flow<Result<List<LessonCategoryEntity>>> {
        return lessonRemoteDataSource.fetchLessonCategoryList()
    }

    override fun fetchLessonSiteList(): Flow<Result<List<LessonSiteEntity>>> {
        return lessonRemoteDataSource.fetchLessonSiteList()
    }

    override fun updateLesson(lessonId: String, lessonUpdateRequestEntity: LessonUpdateRequestEntity): Flow<Result<LessonUpdateEntity>>{
        return lessonRemoteDataSource.updateLesson(lessonId, lessonUpdateRequestEntity)
    }

    override fun fetchLessonStatisticsInformation(): Flow<Result<LessonStatisticsEntity>> {
        return lessonRemoteDataSource.fetchLessonStatisticsInformation()
    }
}