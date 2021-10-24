package com.depromeet.sloth.data.network.register

import androidx.annotation.StringRes
import com.depromeet.sloth.data.model.LessonModel

sealed class RegisterState {
    object Uninitialized: RegisterState()

    object Loading: RegisterState()

    sealed class Success: RegisterState() {

        data class Registered(
            val userName: String,
            val categoryList: List<LessonModel>
        ): Success()

        object NotRegistered: Success()
    }

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable
    ): RegisterState()
}