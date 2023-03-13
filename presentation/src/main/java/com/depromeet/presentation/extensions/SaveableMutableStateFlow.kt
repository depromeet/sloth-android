package com.depromeet.presentation.extensions

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow


// StateHandler 를 사용할 경우 기존의 방식과 값을 수정하고 조작 하는 과정이 달라짐

// 원래 형태, _lessonName 의 혈태도 사라지고 .value 를 통해 값을 set 해주지 못함
//    val lessonName: StateFlow<String> = savedStateHandle.getStateFlow(KEY_LESSON_NAME, lessonDetail.lessonName)
//    private fun setLessonName(lessonName: String) {
//        savedStateHandle[KEY_LESSON_NAME] = lessonName
//    }

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

    //collect, map 과 같은 조작을 하기 위한
    fun asStateFlow(): StateFlow<T> = state
}

// helper function
fun <T> SavedStateHandle.getMutableStateFlow(
    key: String,
    initialValue: T
): SaveableMutableStateFlow<T> =
    SaveableMutableStateFlow(this, key, initialValue)
