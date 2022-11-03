package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.LessonDetail
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.lesson.LessonCategory
import com.depromeet.sloth.data.network.lesson.LessonService
import com.depromeet.sloth.data.network.lesson.LessonSite
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateResponse
import com.depromeet.sloth.ui.common.UiState
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
        emit(UiState.Loading)
        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchTodayLessonList() ?: run {
            emit(UiState.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }

                emit(UiState.Success(response.body() ?: listOf(LessonTodayResponse.EMPTY)))
            }

            401 -> {
                preferenceManager.removeAuthToken()
                emit(UiState.Unauthorized(Exception(response.message())))
            }

            else -> emit(UiState.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(UiState.Error(throwable)) }
        .onCompletion { emit(UiState.UnLoading) }

    override fun fetchAllLessonList() = flow {
        emit(UiState.Loading)

        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchAllLessonList() ?: run {
            emit(UiState.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }

                emit(UiState.Success(response.body() ?: listOf(LessonAllResponse.EMPTY)))
            }

            401 -> {
                preferenceManager.removeAuthToken()
                emit(UiState.Error(Exception(response.message())))
            }

            else -> emit(UiState.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(UiState.Error(throwable)) }
        .onCompletion { emit(UiState.UnLoading) }

    override fun finishLesson(lessonId: String) = flow {
        emit(UiState.Loading)

        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .finishLesson(lessonId) ?: run {
            emit(UiState.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }

                emit(UiState.Success(response.body() ?: LessonFinishResponse.EMPTY))
            }

            401 -> {
                preferenceManager.removeAuthToken()
                emit(UiState.Error(Exception(response.message())))
            }

            else -> emit(UiState.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(UiState.Error(throwable)) }
        .onCompletion { emit(UiState.UnLoading) }

    override suspend fun updateLessonCount(
        count: Int,
        lessonId: Int,
    ): UiState<LessonUpdateCountResponse> {
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

                        UiState.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                    }

                    401 -> {
                        preferenceManager.removeAuthToken()
                        UiState.Error(Exception(message()))
                    }

                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchLessonDetail(
        lessonId: String,
    ): UiState<LessonDetail> {
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

                        UiState.Success(this.body() ?: LessonDetail.EMPTY)
                    }

                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
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
    ): UiState<LessonRegisterResponse> {
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

                        UiState.Success(this.body() ?: LessonRegisterResponse.EMPTY)
                    }

                    401 -> {
                        preferenceManager.removeAuthToken()
                        UiState.Error(Exception(message()))
                    }

                    else -> UiState.Error(java.lang.Exception(message()))
                }
            } ?: return UiState.Error(Exception("Register Exception"))
    }

    override suspend fun deleteLesson(lessonId: String): UiState<LessonDeleteResponse> {
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

                        UiState.Success(this.body() ?: LessonDeleteResponse.EMPTY)
                    }

                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchLessonCategoryList(): UiState<List<LessonCategory>> {
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

                        UiState.Success(this.body() ?: listOf(LessonCategory.EMPTY))
                    }
                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun fetchLessonSiteList(): UiState<List<LessonSite>> {
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

                        UiState.Success(this.body() ?: listOf(LessonSite.EMPTY))
                    }
                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest,
    ): UiState<LessonUpdateResponse> {
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

                        UiState.Success(this.body() ?: LessonUpdateResponse.EMPTY)
                    }

                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }
}