package com.depromeet.sloth.domain.use_case.lesson

import com.depromeet.sloth.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLessonCategoryListUseCase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    operator fun invoke(): Flow<Result<List<LessonCategoryResponse>>> {
        return lessonRepository.fetchLessonCategoryList()
    }
}