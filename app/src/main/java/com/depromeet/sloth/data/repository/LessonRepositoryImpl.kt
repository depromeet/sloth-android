package com.depromeet.sloth.data.repository

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.request.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateCountRequest
import com.depromeet.sloth.data.model.request.lesson.LessonUpdateRequest
import com.depromeet.sloth.data.model.response.lesson.*
import com.depromeet.sloth.data.network.service.LessonService
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
    private val service: LessonService
) : LessonRepository {

    override fun fetchTodayLessonList() = flow {
        emit(Result.Loading)
        val response = service.fetchTodayLessonList(preferenceManager.getAccessToken()) ?: run {
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
        val response = service.fetchAllLessonList(preferenceManager.getAccessToken()) ?: run {
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
        val response = service.finishLesson(preferenceManager.getAccessToken(), lessonId) ?: run {
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
        service.updateLessonCount(
            preferenceManager.getAccessToken(),
            LessonUpdateCountRequest(count, lessonId)
        )?.run {
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

    override fun fetchLessonDetail(lessonId: String) = flow {
        emit(Result.Loading)
        val response =
            service.fetchLessonDetail(preferenceManager.getAccessToken(), lessonId) ?: run {
                emit(Result.Error(Exception("Response is null")))
                return@flow
            }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonDetailResponse.EMPTY))
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

    override fun registerLesson(lessonRegisterRequest: LessonRegisterRequest) = flow {
        emit(Result.Loading)
        val response =
            service.registerLesson(preferenceManager.getAccessToken(), lessonRegisterRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonRegisterResponse.EMPTY))
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

    override fun deleteLesson(lessonId: String) = flow {
        emit(Result.Loading)
        val response = service.deleteLesson(preferenceManager.getAccessToken(), lessonId) ?: run {
            emit(Result.Error(Exception("Response is null")))
            return@flow
        }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonDeleteResponse.EMPTY))
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


    override suspend fun fetchLessonCategoryList(): Result<List<LessonCategoryResponse>> {
        service.fetchLessonCategoryList(preferenceManager.getAccessToken())?.run {
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

//    override fun fetchLessonCategoryList() = flow {
//        emit(Result.Loading)
//        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchLessonCategoryList() ?: run {
//            emit(Result.Error(Exception("Response is null")))
//            return@flow
//        }
//        when (response.code()) {
//            200 -> {
//                val newAccessToken = response.headers()["Authorization"] ?: ""
//                if (newAccessToken.isNotEmpty()) {
//                    preferenceManager.updateAccessToken(newAccessToken)
//                }
//                emit(Result.Success(response.body() ?: LessonCategoryResponse.EMPTY))
//            }
//            401 -> {
//                preferenceManager.removeAuthToken()
//                emit(Result.Unauthorized(Exception(response.message())))
//            }
//            else -> emit(Result.Error(Exception(response.message())))
//        }
//    }
//        .catch { throwable -> emit(Result.Error(throwable)) }
//        .onCompletion { emit(Result.UnLoading) }

    override suspend fun fetchLessonSiteList(): Result<List<LessonSiteResponse>> {
        service.fetchLessonSiteList(preferenceManager.getAccessToken())?.run {
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

//    override fun fetchLessonSiteList() = flow {
//        emit(Result.Loading)
//        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchLessonSiteList() ?: run {
//            emit(Result.Error(Exception("Response is null")))
//            return@flow
//        }
//        when (response.code()) {
//            200 -> {
//                val newAccessToken = response.headers()["Authorization"] ?: ""
//                if (newAccessToken.isNotEmpty()) {
//                    preferenceManager.updateAccessToken(newAccessToken)
//                }
//                emit(Result.Success(response.body() ?: LessonSiteResponse.EMPTY))
//            }
//            401 -> {
//                preferenceManager.removeAuthToken()
//                emit(Result.Unauthorized(Exception(response.message())))
//            }
//            else -> emit(Result.Error(Exception(response.message())))
//        }
//    }
//        .catch { throwable -> emit(Result.Error(throwable)) }
//        .onCompletion { emit(Result.UnLoading) }

    override fun updateLesson(
        lessonId: String,
        lessonUpdateRequest: LessonUpdateRequest
    ) = flow {
        emit(Result.Loading)
        val response =
            service.updateLesson(preferenceManager.getAccessToken(), lessonId, lessonUpdateRequest)
                ?: run {
                    emit(Result.Error(Exception("Response is null")))
                    return@flow
                }
        when (response.code()) {
            200 -> {
                val newAccessToken = response.headers()["Authorization"] ?: ""
                if (newAccessToken.isNotEmpty()) {
                    preferenceManager.updateAccessToken(newAccessToken)
                }
                emit(Result.Success(response.body() ?: LessonUpdateResponse.EMPTY))
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
}