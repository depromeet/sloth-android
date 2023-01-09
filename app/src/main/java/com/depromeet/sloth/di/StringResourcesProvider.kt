package com.depromeet.sloth.di

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

//TODO 왜 Singleton annotation 이 붙어야 하는지

// viewModel 내에서 stringResource 에 접근을 하기 위한 용도
@Singleton
class StringResourcesProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(@StringRes stringResId: Int): String {
        return context.getString(stringResId)
    }
}
