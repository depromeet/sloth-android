package com.depromeet.domain.usecase.lesson

import com.depromeet.domain.entity.LessonRegisterEntity
import com.depromeet.domain.entity.LessonRegisterRequestEntity
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RegisterLessonUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonRegisterRequest: LessonRegisterRequestEntity): Flow<Result<LessonRegisterEntity>> {
        return lessonRepository.registerLesson(lessonRegisterRequest)
    }
}