package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchLessonDetailUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(lessonId: String): Flow<Result<LessonDetailResponse>> {
        return lessonRepository.fetchLessonDetail(lessonId)
    }
}