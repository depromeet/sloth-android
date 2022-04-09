package com.depromeet.sloth.extensions

import android.content.Context
import com.depromeet.sloth.util.LoadingDialogUtil

fun handleLoadingState(context: Context) {
    LoadingDialogUtil.showProgress(context)
}