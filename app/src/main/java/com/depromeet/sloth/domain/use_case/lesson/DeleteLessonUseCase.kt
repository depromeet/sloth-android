package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.data.model.response.lesson.LessonDeleteResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository,
) {
    operator fun invoke(lessonId: String): Flow<Result<LessonDeleteResponse>> {
        return lessonRepository.deleteLesson(lessonId)
    }
}