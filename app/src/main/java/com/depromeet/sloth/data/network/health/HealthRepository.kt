package com.depromeet.sloth.data.network.health

import com.depromeet.sloth.data.PreferenceManager
import javax.inject.Inject

class HealthRepository @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
//    suspend fun getHealth(): HealthState<HealthResponse> {
//        RetrofitServiceGenerator.build("Input your test token")
//            .create(HealthService::class.java)
//            .fetchHealth()?.run {
//                return HealthState.Success(
//                    this.body() ?: HealthResponse()
//                )
//            } ?: return HealthState.Error(Exception("Retrofit Exception"))
//    }
//
//    suspend fun fetchTodayLessonList(): LessonState<List<LessonTodayResponse>> {
//        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(LessonService::class.java)
//            .fetchTodayLessonList()?.run {
//                return when (this.code()) {
//                    200 -> {
//                        val newAccessToken = headers()["Authorization"] ?: ""
//                        if(newAccessToken.isNotEmpty()) preferenceManager.updateAccessToken(newAccessToken)
//
//                        LessonState.Success(body() ?: listOf(LessonTodayResponse.EMPTY))
//                    }
//                    else -> LessonState.Error(Exception(message()))
//                }
//            } ?: return LessonState.Error(Exception("Retrofit Exception"))
//    }
}

