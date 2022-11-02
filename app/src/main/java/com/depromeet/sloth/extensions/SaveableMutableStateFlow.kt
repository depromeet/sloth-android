package com.depromeet.sloth.extensions

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

// StateHandler 를 사용할 경우 기존의 방식과 값을 수정하고 조작 하는 과정이 달라짐
// 기존의 방법과 일관성 있는 값 수정, 사용을 위한 helper class
class SaveableMutableStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    initialValue: T
) {
    private val state: StateFlow<T> = savedStateHandle.getStateFlow(key, initialValue)
    var value: T
        get() = state.value
        set(value) {
            savedStateHandle[key] = value
        }

    fun asStateFlow(): StateFlow<T> = state
}

fun <T> SavedStateHandle.getMutableStateFlow(
    key: String,
    initialValue: T
): SaveableMutableStateFlow<T> =
    SaveableMutableStateFlow(this, key, initialValue)
