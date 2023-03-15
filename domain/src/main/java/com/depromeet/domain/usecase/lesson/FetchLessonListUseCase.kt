package com.depromeet.domain.usecase.lesson

import com.depromeet.domain.entity.LessonEntity
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchLessonListUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(): Flow<Result<List<LessonEntity>>> {
        return lessonRepository.fetchLessonList()
    }
}