package com.depromeet.sloth.ui.home

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.depromeet.sloth.R

class ForbiddenDialog(context: Context) {
    lateinit var listener: ForbiddenDialogClickedListener
    lateinit var btnConfirm: Button

    private val dlg = Dialog(context)

    interface ForbiddenDialogClickedListener {
        fun onConfirmClicked()
    }

    fun start() {
        /*타이틀바 제거*/
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*커스텀 다이얼로그 radius 적용*/
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        /*영역 외 클릭시 창이 사라지지 않도록*/
        dlg.setCancelable(false)
        dlg.setContentView(R.layout.dialog_home_forbidden)


        btnConfirm = dlg.findViewById(R.id.btn_confirm)
        btnConfirm.setOnClickListener {
            listener.onConfirmClicked()
            dlg.dismiss()
        }
        dlg.show()
    }
}


