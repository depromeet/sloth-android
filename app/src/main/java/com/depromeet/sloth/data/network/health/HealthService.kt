package com.depromeet.sloth.data.network.health

import retrofit2.Response
import retrofit2.http.GET

interface HealthService {

    @GET("api/health")
    suspend fun fetchHealth(): Response<HealthResponse>?
}