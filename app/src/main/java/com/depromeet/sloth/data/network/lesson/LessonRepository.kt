package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.RetrofitServiceGeneratorTest
import com.depromeet.sloth.data.network.lesson.category.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailResponse
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.site.LessonSiteResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import javax.inject.Inject

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */
class LessonRepository @Inject constructor(
    private val retrofitServiceGeneratorTest: RetrofitServiceGeneratorTest,
    private val preferenceManager: PreferenceManager
) {
    suspend fun deleteLesson(lessonId: String): LessonDeleteState<LessonDeleteResponse> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .deleteLesson(lessonId)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonDeleteState.Success(this.body() ?: LessonDeleteResponse())
                    }
                    else -> LessonDeleteState.Error(Exception(message()))
                }
            } ?: return LessonDeleteState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchTodayLessonList(): LessonState<List<LessonTodayResponse>> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchTodayLessonList()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonState.Success(body() ?: listOf(LessonTodayResponse.EMPTY))
                    }
                    else -> LessonState.Error(java.lang.Exception(message()))
                }
            } ?: return LessonState.Error(java.lang.Exception("Retrofit Exception"))
    }

    suspend fun fetchAllLessonList(): LessonState<List<LessonAllResponse>> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchAllLessonList()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonState.Success(this.body() ?: listOf(LessonAllResponse.EMPTY))
                    }
                    else -> LessonState.Error(Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateLessonCount(
        count: Int,
        lessonId: Int
    ): LessonState<LessonUpdateCountResponse> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .updateLessonCount(LessonUpdateCountRequest(count, lessonId))?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonState.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                    }
                    else -> LessonState.Error(Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchLessonDetail(
        lessonId: String
    ): LessonDetailState<LessonDetailResponse> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonDetail(lessonId)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonDetailState.Success(this.body() ?: LessonDetailResponse())
                    }
                    else -> LessonDetailState.Error(Exception(message()))
                }
            } ?: return LessonDetailState.Error(Exception("Retrofit Exception"))
    }

    suspend fun registerLesson(
        request: LessonRegisterRequest
    ): LessonState<LessonRegisterResponse> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .registerLesson(request)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonState.Success(this.body() ?: LessonRegisterResponse())
                    }
                    else -> LessonState.Error(java.lang.Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Register Exception"))
    }

    suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategoryResponse>> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonCategoryList()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonState.Success(this.body() ?: listOf())
                    }
                    else -> LessonState.Error(Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchLessonSiteList(): LessonState<List<LessonSiteResponse>> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonSiteList()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonState.Success(this.body() ?: listOf())
                    }
                    else -> LessonState.Error(Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest
    ): LessonUpdateState<LessonUpdateResponse> {
        retrofitServiceGeneratorTest.build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .updateLesson(lessonId, updateLessonRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        LessonUpdateState.Success(this.body() ?: LessonUpdateResponse())
                    }
                    else -> LessonUpdateState.Error(Exception(message()))
                }
            } ?: return LessonUpdateState.Error(Exception("Retrofit Exception"))
    }
}