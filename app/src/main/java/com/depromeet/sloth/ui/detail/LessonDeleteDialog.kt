package com.depromeet.sloth.ui.detail

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.depromeet.sloth.R

class LessonDeleteDialog(context: Context) {

    lateinit var listener: LessonDeleteDialogClickedListener
    lateinit var btnDelete: Button
    lateinit var btnCancel: Button

    interface LessonDeleteDialogClickedListener {
        fun onDeleteClicked()
    }

    private val dlg = Dialog(context)

    fun start() {
        /*타이틀바 제거*/
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*커스텀 다이얼로그 radius 적용*/
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_lesson_detail_delete)

        btnDelete = dlg.findViewById(R.id.btn_detail_delete)
        btnDelete.setOnClickListener {
            listener.onDeleteClicked()
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.btn_detail_delete_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}


