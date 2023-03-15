package com.depromeet.domain.usecase.lesson

import com.depromeet.domain.entity.LessonFinishEntity
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FinishLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonId: String): Flow<Result<LessonFinishEntity>> {
        return lessonRepository.finishLesson(lessonId)
    }
}