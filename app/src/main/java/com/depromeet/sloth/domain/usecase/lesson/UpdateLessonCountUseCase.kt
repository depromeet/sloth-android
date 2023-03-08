package com.depromeet.sloth.domain.usecase.lesson

import com.depromeet.sloth.data.model.response.lesson.UpdateLessonCountResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateLessonCountUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(count: Int, lessonId: Int): Flow<Result<UpdateLessonCountResponse>> {
        return lessonRepository.updateLessonCount(count, lessonId)
    }
}