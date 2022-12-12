package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.lesson.LessonTodayResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayLessonListUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(): Flow<Result<List<LessonTodayResponse>>> {
        return lessonRepository.fetchTodayLessonList()
    }
}