package com.depromeet.sloth.util

import android.content.Context
import com.depromeet.sloth.ui.custom.LoadingDialog

/**
 * @author 최철훈
 * @created 2022-01-02
 * @desc 네트워크 통신시 사용되는 Dialog Util
 */
object LoadingDialogUtil {
    private var loadingDialog: LoadingDialog? = null

    fun showProgress(context: Context) {
        loadingDialog?.run {
            loadingDialog?.show()
        } ?: kotlin.run {
            loadingDialog = LoadingDialog(context)
            showProgress(context)
        }
    }

    fun hideProgress() {
        loadingDialog?.run { dismiss() }
        loadingDialog = null
    }
}