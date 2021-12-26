package com.depromeet.sloth.ui.manage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.depromeet.sloth.R

class WithdrawDialog(context: Context) {

    lateinit var listener: WithdrawDialogClickedListener
    lateinit var btnWithdraw: Button
    lateinit var btnCancel: Button

    private val dlg = Dialog(context)

    interface WithdrawDialogClickedListener {
        fun onWithdrawClicked()
    }

    fun start() {
        /*타이틀바 제거*/
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*커스텀 다이얼로그 radius 적용*/
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_mypage_withdraw)

        btnWithdraw = dlg.findViewById(R.id.btn_withdraw)
        btnWithdraw.setOnClickListener {
            listener.onWithdrawClicked()
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.btn_withdraw_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}


