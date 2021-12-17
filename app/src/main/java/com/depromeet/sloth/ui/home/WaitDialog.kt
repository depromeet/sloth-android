package com.depromeet.sloth.ui.home

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.depromeet.sloth.R
import com.depromeet.sloth.ui.detail.LessonDeleteDialog

class WaitDialog(context: Context) {
    lateinit var btnConfirm: Button

    private val dlg = Dialog(context)

    interface WithdrawDialogClickedListener {
        fun onWithdrawClicked()
    }

    fun start() {
        /*타이틀바 제거*/
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*커스텀 다이얼로그 radius 적용*/
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_home_wait)

        btnConfirm = dlg.findViewById(R.id.btn_confirm)
        btnConfirm.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}


