package com.depromeet.sloth.data.network.lesson

import android.util.Log
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.*
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteResponse
import com.depromeet.sloth.data.network.lesson.delete.LessonDeleteState
import com.depromeet.sloth.data.network.lesson.detail.LessonDetailState
import com.depromeet.sloth.data.network.lesson.list.*
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.register.LessonRegisterResponse
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateRequest
import com.depromeet.sloth.data.network.lesson.update.LessonUpdateState
import com.depromeet.sloth.ui.base.UIState
import kotlinx.coroutines.flow.*
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

    override fun fetchAllLessonList() = flow {
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

    override fun finishLesson(lessonId: String) = flow {
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

    override suspend fun updateLessonCount(
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

//    override suspend fun fetchLessonDetail(
//        lessonId: String,
//    ): LessonDetailState<LessonDetailResponse> {
//        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchLessonDetail(lessonId)?.run {
//                return when (this.code()) {
//                    200 -> {
//                        val newAccessToken = headers()["Authorization"] ?: ""
//                        if (newAccessToken.isNotEmpty()) {
//                            preferenceManager.updateAccessToken(newAccessToken)
//                        }
//
//                        LessonDetailState.Success(this.body() ?: LessonDetailResponse())
//                    }
//                    else -> LessonDetailState.Error(Exception(message()))
//                }
//            } ?: return LessonDetailState.Error(Exception("Retrofit Exception"))
//    }

    override suspend fun fetchLessonDetail(
        lessonId: String,
    ): LessonDetailState<LessonDetail> {
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

                        LessonDetailState.Success(this.body() ?: LessonDetail())
                    }
                    else -> LessonDetailState.Error(Exception(message()))
                }
            } ?: return LessonDetailState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun registerLesson(
        lessonRegisterRequest: LessonRegisterRequest,
    ): LessonState<LessonRegisterResponse> {
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

    override suspend fun deleteLesson(lessonId: String): LessonDeleteState<LessonDeleteResponse> {
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

//    override fun fetchLessonCategoryList(): Flow<UIState<List<LessonCategory>>> = flow<UIState<List<LessonCategory>>> {
//        emit(UIState.Loading)
//        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchLessonCategoryList() ?: run {
//            emit(UIState.Error(Exception("Response is null")))
//            return@flow
//        }
//
//        when (response.code()) {
//            200 -> {
//                val newAccessToken = response.headers()["Authorization"] ?: ""
//                if (newAccessToken.isNotEmpty()) {
//                    preferenceManager.updateAccessToken(newAccessToken)
//                }
//                //map, list 변환 과정이 여기에 있어야
//
//                emit(UIState.Success(response.body() ?: listOf(LessonCategory.EMPTY)))
//            }
//            401 -> {
//                preferenceManager.removeAuthToken()
//                emit(UIState.Unauthorized(Exception(response.message())))
//            }
//            else -> emit(UIState.Error(Exception(response.message())))
//        }
//    }
//        .catch { throwable -> emit(UIState.Error(throwable)) }
//        .onCompletion { emit(UIState.UnLoading) }

//    override fun fetchLessonSiteList(): Flow<UIState<List<LessonSite>>> = flow<UIState<List<LessonSite>>> {
//        emit(UIState.Loading)
//        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchLessonSiteList() ?: run {
//            emit(UIState.Error(Exception("Response is null")))
//            return@flow
//        }
//
//        when (response.code()) {
//            200 -> {
//                val newAccessToken = response.headers()["Authorization"] ?: ""
//                if (newAccessToken.isNotEmpty()) {
//                    preferenceManager.updateAccessToken(newAccessToken)
//                }
//                //map, list 변환 과정이 여기에 있어야
//
//                emit(UIState.Success(response.body() ?: listOf(LessonSite.EMPTY)))
//            }
//            401 -> {
//                preferenceManager.removeAuthToken()
//                emit(UIState.Unauthorized(Exception(response.message())))
//            }
//            else -> emit(UIState.Error(Exception(response.message())))
//        }
//    }
//        .catch { throwable -> emit(UIState.Error(throwable)) }
//        .onCompletion { emit(UIState.UnLoading) }

    override suspend fun fetchLessonCategoryList(): LessonState<List<LessonCategory>> {
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

    override suspend fun fetchLessonSiteList(): LessonState<List<LessonSite>> {
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

    override suspend fun updateLesson(
        lessonId: String,
        updateLessonRequest: LessonUpdateRequest,
    ): LessonUpdateState<LessonUpdate> {
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

                        LessonUpdateState.Success(this.body() ?: LessonUpdate())
                    }
                    else -> LessonUpdateState.Error(Exception(message()))
                }
            } ?: return LessonUpdateState.Error(Exception("Retrofit Exception"))
    }
}