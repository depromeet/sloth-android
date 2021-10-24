package com.depromeet.sloth.data.network.health

import com.depromeet.sloth.data.network.ServiceGenerator
import java.lang.Exception

class HealthRepository {
    suspend fun getHealth(): HealthState<HealthResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = "Input your test url",
            authToken = "Input your test token"
        )
            .create(HealthService::class.java)
            .fetchHealth()?.run {
            return HealthState.Success(
                this.body() ?: HealthResponse()
            )
        } ?: return HealthState.Error(Exception("Retrofit Exception"))
    }
}