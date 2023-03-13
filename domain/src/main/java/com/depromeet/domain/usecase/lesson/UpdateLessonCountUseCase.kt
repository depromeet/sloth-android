package com.depromeet.domain.usecase.lesson

import com.depromeet.domain.entity.UpdateLessonCountEntity
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UpdateLessonCountUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(count: Int, lessonId: Int): Flow<Result<UpdateLessonCountEntity>> {
        return lessonRepository.updateLessonCount(count, lessonId)
    }
}