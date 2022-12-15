package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.data.model.response.lesson.LessonAllResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLessonListUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(): Flow<Result<List<LessonAllResponse>>> {
        return lessonRepository.fetchAllLessonList()
    }
}