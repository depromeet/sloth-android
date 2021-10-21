package com.depromeet.sloth.data.network.health

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.ServiceGenerator.createService
import java.lang.Exception

class HealthRepository {
    suspend fun getHealth(): HealthState<HealthResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.TARGET_SERVER,
            authToken = "Nothing"
        ).createService(
            serviceClass = HealthService::class.java
        ).fetchHealth()?.run {
            return HealthState.Success(
                this.body() ?: HealthResponse()
            )
        } ?: return HealthState.Error(Exception("Retrofit Exception"))
    }
}