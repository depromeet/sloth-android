package com.depromeet.sloth.ui.manage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.depromeet.sloth.R

class LogoutDialog(context: Context) {

    lateinit var listener: LogoutDialogClickedListener
    lateinit var btnLogout: Button
    lateinit var btnCancel: Button

    private val dlg = Dialog(context)

    interface LogoutDialogClickedListener {
        fun onLogoutClicked()
    }

    fun start() {
        /*타이틀바 제거*/
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*커스텀 다이얼로그 radius 적용*/
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_mypage_logout)

        btnLogout = dlg.findViewById(R.id.btn_logout)
        btnLogout.setOnClickListener {
            listener.onLogoutClicked()
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.btn_logout_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}


