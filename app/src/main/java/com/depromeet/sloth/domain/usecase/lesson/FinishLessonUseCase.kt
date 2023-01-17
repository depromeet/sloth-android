package com.depromeet.sloth.domain.usecase.lesson

import com.depromeet.sloth.data.model.response.lesson.LessonFinishResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FinishLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonId: String): Flow<Result<LessonFinishResponse>> {
        return lessonRepository.finishLesson(lessonId)
    }
}