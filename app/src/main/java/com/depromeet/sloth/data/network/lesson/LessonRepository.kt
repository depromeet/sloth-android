package com.depromeet.sloth.data.network.lesson

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
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
import com.depromeet.sloth.ui.base.UIState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

/**
 * @author 최철훈
 * @created 2021-12-28
 * @desc Lesson 관련 API 저장소
 */
class LessonRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    suspend fun deleteLesson(lessonId: String): LessonDeleteState<LessonDeleteResponse> {
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

                        LessonDeleteState.Success(this.body() ?: LessonDeleteResponse())
                    }
                    else -> LessonDeleteState.Error(Exception(message()))
                }
            } ?: return LessonDeleteState.Error(Exception("Retrofit Exception"))
    }

    fun fetchTodayLessonList() = flow {
        emit(UIState.Loading)

        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchTodayLessonList() ?: run {
            emit(UIState.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }

                emit(UIState.Success(response.body() ?: listOf(LessonTodayResponse.EMPTY)))
            }
            401 -> {
                preferenceManager.removeAuthToken()
                emit(UIState.Unauthorized(Exception(response.message())))
            }
            else -> emit(UIState.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(UIState.Error(throwable)) }
        .onCompletion { emit(UIState.UnLoading) }

//    suspend fun fetchTodayLessonList(): LessonState<List<LessonTodayResponse>> {
//        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchTodayLessonList()?.run {
//                return when (this.code()) {
//                    200 -> {
//                        val newAccessToken = headers()["Authorization"] ?: ""
//                        if (newAccessToken.isNotEmpty()) {
//                            preferenceManager.updateAccessToken(newAccessToken)
//                        }
//
//                        LessonState.Success(body() ?: listOf())
//                    }
//                    401 -> {
//                        preferenceManager.removeAuthToken()
//                        LessonState.Error(Exception(message()))
//                    }
//                    else -> LessonState.Error(Exception(message()))
//                }
//            } ?: return LessonState.Error(Exception("Response is null"))
//    }

    fun fetchAllLessonList() = flow {
        emit(UIState.Loading)

        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .fetchAllLessonList() ?: run {
            emit(UIState.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }

                emit(UIState.Success(response.body() ?: listOf(LessonAllResponse.EMPTY)))
            }
            401 -> {
                preferenceManager.removeAuthToken()
                emit(UIState.Error(Exception(response.message())))
            }
            else -> emit(UIState.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(UIState.Error(throwable)) }
        .onCompletion { emit(UIState.UnLoading) }

    fun finishLesson(lessonId: String) = flow {
        emit(UIState.Loading)

        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(LessonService::class.java)
            .finishLesson(lessonId) ?: run {
            emit(UIState.Error(Exception("Response is null")))
            return@flow
        }

        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }

                emit(UIState.Success(response.body() ?: LessonFinishResponse.EMPTY))
            }
            401 -> {
                preferenceManager.removeAuthToken()
                emit(UIState.Error(Exception(response.message())))
            }
            else -> emit(UIState.Error(Exception(response.message())))
        }
    }
        .catch { throwable -> emit(UIState.Error(throwable)) }
        .onCompletion { emit(UIState.UnLoading) }

//    suspend fun fetchAllLessonList(): LessonState<List<LessonAllResponse>> {
//        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchAllLessonList()?.run {
//                return when (this.code()) {
//                    200 -> {
//                        val newAccessToken = headers()["Authorization"] ?: ""
//                        if (newAccessToken.isNotEmpty()) {
//                            preferenceManager.updateAccessToken(newAccessToken)
//                        }
//
//                        LessonState.Success(body() ?: listOf())
//                    }
//                    401 -> {
//                        preferenceManager.removeAuthToken()
//                        LessonState.Error(Exception(message()))
//                    }
//                    else -> LessonState.Error(Exception(message()))
//                }
//            } ?: return LessonState.Error(Exception("Response is null"))
//    }

    suspend fun updateLessonCount(
        count: Int,
        lessonId: Int
    ): LessonState<LessonUpdateCountResponse> {
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

                        LessonState.Success(this.body() ?: LessonUpdateCountResponse.EMPTY)
                    }
                    401 -> {
                        preferenceManager.removeAuthToken()
                        LessonState.Error(Exception(message()))
                    }
                    else -> LessonState.Error(Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchLessonDetail(
        lessonId: String
    ): LessonDetailState<LessonDetailResponse> {
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

                        LessonDetailState.Success(this.body() ?: LessonDetailResponse())
                    }
                    else -> LessonDetailState.Error(Exception(message()))
                }
            } ?: return LessonDetailState.Error(Exception("Retrofit Exception"))
    }

    suspend fun registerLesson(
        request: LessonRegisterRequest
    ): LessonState<LessonRegisterResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
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
                    401 -> {
                        preferenceManager.removeAuthToken()
                        LessonState.Error(Exception(message()))
                    }
                    else -> LessonState.Error(java.lang.Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Register Exception"))
    }

    suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategoryResponse>> {
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

                        LessonState.Success(this.body() ?: listOf())
                    }
                    else -> LessonState.Error(Exception(message()))
                }
            } ?: return LessonState.Error(Exception("Retrofit Exception"))
    }

    suspend fun fetchLessonSiteList(): LessonState<List<LessonSiteResponse>> {
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

                        LessonUpdateState.Success(this.body() ?: LessonUpdateResponse())
                    }
                    else -> LessonUpdateState.Error(Exception(message()))
                }
            } ?: return LessonUpdateState.Error(Exception("Retrofit Exception"))
    }
}