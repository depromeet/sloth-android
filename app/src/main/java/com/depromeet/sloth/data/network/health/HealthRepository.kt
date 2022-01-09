package com.depromeet.sloth.data.network.health

import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import java.lang.Exception
import javax.inject.Inject

class HealthRepository @Inject constructor() {
    suspend fun getHealth(): HealthState<HealthResponse> {
        RetrofitServiceGenerator.build("Input your test token")
            .create(HealthService::class.java)
            .fetchHealth()?.run {
                return HealthState.Success(
                    this.body() ?: HealthResponse()
                )
            } ?: return HealthState.Error(Exception("Retrofit Exception"))
    }
}