package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.common.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonId: String, lessonUpdateRequest: LessonUpdateRequest): Flow<Result<LessonUpdateResponse>> {
        return lessonRepository.updateLesson(lessonId, lessonUpdateRequest)
    }
}