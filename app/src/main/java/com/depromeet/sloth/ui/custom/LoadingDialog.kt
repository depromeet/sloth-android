package com.depromeet.sloth.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.depromeet.sloth.R

class LoadingDialog(
    context: Context
) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setContentView(R.layout.view_loading_dialog)
        setCanceledOnTouchOutside(false)
    }
}