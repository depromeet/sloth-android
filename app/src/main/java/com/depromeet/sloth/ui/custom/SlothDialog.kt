package com.depromeet.sloth.ui.custom

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.depromeet.sloth.R

class SlothDialog(context: Context, private val state: DialogState) {

    lateinit var onItemClickListener: OnItemClickedListener

    private val dlg = Dialog(context)

    interface OnItemClickedListener {
        fun onItemClicked()
    }

    fun show() = with(dlg) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // custom view 영역 size 적용
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_sloth)

        val ivDialogState = findViewById<ImageView>(R.id.iv_dialog_state)
        val btnDialogCheck = findViewById<AppCompatButton>(R.id.btn_dialog_check)
        val btnDialogCancel = findViewById<AppCompatButton>(R.id.btn_dialog_cancel)
        val tvDialogMessage = findViewById<TextView>(R.id.tv_dialog_message)

        when (state) {
            DialogState.FORBIDDEN -> {
                ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
                tvDialogMessage.setText(R.string.forbidden_dialog_message)
                btnDialogCancel.visibility = View.GONE
                btnDialogCheck.background = AppCompatResources.getDrawable(
                    context, R.drawable.bg_home_rounded_sloth
                )

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dismiss()
                }
            }

            DialogState.LOGOUT -> {
                tvDialogMessage.setText(R.string.logout_dialog_message)

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dismiss()
                }
            }

            DialogState.WITHDRAW -> {
                tvDialogMessage.setText(R.string.withdraw_dialog_message)

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dismiss()
                }

            }

            DialogState.DELETE_LESSON -> {
                tvDialogMessage.setText(R.string.delete_lesson_dialog_message)
                btnDialogCheck.setText(R.string.delete_lesson_dialog_message_check)

                btnDialogCheck.setOnClickListener {
                    onItemClickListener.onItemClicked()
                    dismiss()
                }
            }

            DialogState.WAIT -> {
                ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
                tvDialogMessage.setText(R.string.wait_dialog_message)
                btnDialogCancel.visibility = View.GONE
                btnDialogCheck.background = AppCompatResources.getDrawable(
                    context, R.drawable.bg_home_rounded_sloth
                )

                btnDialogCheck.setOnClickListener {
                    dismiss()
                }
            }

            DialogState.COMPLETE -> {
                tvDialogMessage.setText(R.string.check_all_lesson_finished)
                ivDialogState.setColorFilter(ContextCompat.getColor(context, R.color.sloth))
                btnDialogCheck.apply {
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sloth))
                    text = context.getString(R.string.finished)
                    setOnClickListener {
                        onItemClickListener.onItemClicked()
                        dismiss()
                    }
                }
            }
        }

        btnDialogCancel.setOnClickListener {
            dismiss()
        }

        show()
    }
}

enum class DialogState {
    FORBIDDEN, LOGOUT, WITHDRAW, DELETE_LESSON, WAIT, COMPLETE
}