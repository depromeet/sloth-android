package com.depromeet.sloth.domain.usecase.lesson

import com.depromeet.sloth.data.model.response.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import javax.inject.Inject

class UpdateLessonCountUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(count: Int, lessonId: Int): Result<LessonUpdateCountResponse> {
        return lessonRepository.updateLessonCount(count, lessonId)
    }

//    operator fun invoke(count: Int, lessonId: Int): Flow<Result<LessonUpdateCountResponse>> {
//        return lessonRepository.updateLessonCount(count, lessonId)
//    }
}