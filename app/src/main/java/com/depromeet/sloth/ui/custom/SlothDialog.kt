package com.depromeet.sloth.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.R

class SlothDialog(private val context: Context, private val state: DialogState) {

    lateinit var onItemClickListener: OnItemClickedListener

    private val dlg = Dialog(context)

    interface OnItemClickedListener {
        fun onItemClicked()
    }

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_sloth)

        val ivDialogState = dlg.findViewById<ImageView>(R.id.iv_dialog_state)
        val btnDialogCheck = dlg.findViewById<AppCompatButton>(R.id.btn_dialog_check)
        val btnDialogCancel = dlg.findViewById<AppCompatButton>(R.id.btn_dialog_cancel)
        val tvDialogMessage = dlg.findViewById<TextView>(R.id.tv_dialog_message)

        when (state) {
            DialogState.FORBIDDEN -> {
                ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
                tvDialogMessage.setText(R.string.forbidden_dialog_message)
                btnDialogCancel.visibility = View.GONE
                btnDialogCheck.background = AppCompatResources.getDrawable(
                    context,
                    R.drawable.bg_home_rounded_sloth
                )

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dlg.dismiss()
                }
            }

            DialogState.LOGOUT -> {
                tvDialogMessage.setText(R.string.logout_dialog_message)

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dlg.dismiss()
                }
            }

            DialogState.WITHDRAW -> {
                tvDialogMessage.setText(R.string.withdraw_dialog_message)

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dlg.dismiss()
                }

            }

            DialogState.DELETE_LESSON -> {
                tvDialogMessage.setText(R.string.delete_lesson_dialog_message)

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dlg.dismiss()
                }
            }

            DialogState.WAIT -> {
                ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
                tvDialogMessage.setText(R.string.wait_dialog_message)
                btnDialogCancel.visibility = View.GONE
                btnDialogCheck.background = AppCompatResources.getDrawable(
                    context,
                    R.drawable.bg_home_rounded_sloth
                )

                btnDialogCheck.setOnClickListener {
                    dlg.dismiss()
                }
            }
        }
        btnDialogCancel.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}

enum class DialogState {
    FORBIDDEN, LOGOUT, WITHDRAW, DELETE_LESSON, WAIT
}