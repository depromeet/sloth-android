package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.response.lesson.LessonDetailResponse
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.model.response.lesson.LessonCategoryResponse
import com.depromeet.sloth.data.network.service.LessonService
import com.depromeet.sloth.data.model.response.lesson.LessonSiteResponse
import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.response.lesson.LessonRegisterResponse
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.LessonUpdateResponse
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateCountRequest
import com.depromeet.sloth.data.model.response.lesson.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */
class LessonRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
) : LessonRepository {

    override fun fetchTodayLessonList() = flow {
        emit(Result.Loading)
        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchTodayLessonList() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonTodayResponse.EMPTY)))
            }
            401 -> {
                preferenceManager.removeAuthToken()
                emit(Result.Unauthorized(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun fetchAllLessonList() = flow {
        emit(Result.Loading)
        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchAllLessonList() ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: listOf(LessonAllResponse.EMPTY)))
            }
            401 -> {
                preferenceManager.removeAuthToken()
                emit(Result.Error(Exception(response.message())))
            }
            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override fun finishLesson(lessonId: String) = flow {
        emit(Result.Loading)

        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .finishLesson(lessonId) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }

                emit(Result.Success(response.body() ?: LessonFinishResponse.EMPTY))
            }

            401 -> {
                preferenceManager.removeAuthToken()
                emit(Result.Error(Exception(response.message())))
            }

            else -> emit(Result.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(Result.Error(throwable)) }
        .onCompletion { emit(Result.UnLoading) }

    override suspend fun updateLessonCount(
        count: Int,
        lessonId: Int,
    ): Result<LessonUpdateCountResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .updateLessonCount(LessonUpdateCountRequest(count, lessonId))?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                    }

                    401 -> {
                        preferenceManager.removeAuthToken()
                        Result.Error(Exception(message()))
                    }

                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchLessonDetail(
        lessonId: String,
    ): Result<LessonDetailResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonDetail(lessonId)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: LessonDetailResponse.EMPTY)
                    }

                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

//    override suspend fun registerLesson(
//        lessonRegisterRequest: LessonRegisterRequest,
//    ): LessonRegisterResponse? {
//        return RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .registerLesson(lessonRegisterRequest).awaitResponse().body()
//    }

    override suspend fun registerLesson(
        lessonRegisterRequest: LessonRegisterRequest,
    ): Result<LessonRegisterResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .registerLesson(lessonRegisterRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: LessonRegisterResponse.EMPTY)
                    }

                    401 -> {
                        preferenceManager.removeAuthToken()
                        Result.Error(Exception(message()))
                    }

                    else -> Result.Error(java.lang.Exception(message()))
                }
            } ?: return Result.Error(Exception("Register Exception"))
    }

    override suspend fun deleteLesson(lessonId: String): Result<LessonDeleteResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .deleteLesson(lessonId)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: LessonDeleteResponse.EMPTY)
                    }

                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchLessonCategoryList(): Result<List<LessonCategoryResponse>> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonCategoryList()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: listOf(LessonCategoryResponse.EMPTY))
                    }
                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchLessonSiteList(): Result<List<LessonSiteResponse>> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchLessonSiteList()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: listOf(LessonSiteResponse.EMPTY))
                    }
                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }

    override suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest,
    ): Result<LessonUpdateResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .updateLesson(lessonId, updateLessonRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        Result.Success(this.body() ?: LessonUpdateResponse.EMPTY)
                    }

                    else -> Result.Error(Exception(message()))
                }
            } ?: return Result.Error(Exception("Retrofit Exception"))
    }
}