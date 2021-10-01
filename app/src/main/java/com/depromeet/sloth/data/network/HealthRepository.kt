package com.depromeet.sloth.data.network

class HealthRepository {

    private val generator = ServiceGenerator()

    suspend fun getHealth() = generator.createService(HealthService::class.java).fetchHealth()
}