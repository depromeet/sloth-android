package com.depromeet.sloth.data.network.health

import java.lang.Exception

sealed class HealthState<out R> {
    data class Success<out T>(val data: T): HealthState<T>()
    data class Error(val exception: Exception): HealthState<Nothing>()
}