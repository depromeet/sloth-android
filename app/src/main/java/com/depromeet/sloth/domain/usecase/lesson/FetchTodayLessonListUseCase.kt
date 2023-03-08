package com.depromeet.sloth.domain.usecase.lesson

import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.lesson.TodayLessonResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTodayLessonListUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(): Flow<Result<List<TodayLessonResponse>>> {
        return lessonRepository.fetchTodayLessonList()
    }
}