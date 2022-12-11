package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateCountResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import javax.inject.Inject

class UpdateLessonCountUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(count: Int, lessonId: Int): Result<LessonUpdateCountResponse> {
        return lessonRepository.updateLessonCount(count, lessonId)
    }
}