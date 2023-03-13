package com.depromeet.domain.usecase.lesson

import com.depromeet.domain.entity.LessonStatisticsEntity
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchLessonStatisticsInfoUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(): Flow<Result<LessonStatisticsEntity>> {
        return lessonRepository.fetchLessonStatisticsInformation()
    }
}