package com.depromeet.sloth.data.network.health

data class HealthResponse(
    val status: Boolean = true,
    val health: String = "default"
)