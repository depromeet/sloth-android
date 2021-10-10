package com.depromeet.sloth.data.network

import java.lang.Exception

class HealthRepository(
    private val generator: ServiceGenerator
) {
    suspend fun getHealth(): HealthState<HealthResponse> {
        generator.createService(HealthService::class.java).fetchHealth()?.run {
            return HealthState.Success(
                this.body() ?: HealthResponse(
                    status = true,
                    health = "default"
                )
            )
        }

        return HealthState.Error(Exception("Retrofit Exception"))
    }
}