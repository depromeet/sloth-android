package com.depromeet.domain.usecase.lesson

import com.depromeet.domain.entity.LessonUpdateEntity
import com.depromeet.domain.entity.LessonUpdateRequestEntity
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonId: String, lessonUpdateRequest: LessonUpdateRequestEntity): Flow<Result<LessonUpdateEntity>> {
        return lessonRepository.updateLesson(lessonId, lessonUpdateRequest)
    }
}