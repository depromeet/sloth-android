package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.response.lesson.LessonRegisterResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.common.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonRegisterRequest: LessonRegisterRequest): Flow<Result<LessonRegisterResponse>> {
        return lessonRepository.registerLesson(lessonRegisterRequest)
    }
}