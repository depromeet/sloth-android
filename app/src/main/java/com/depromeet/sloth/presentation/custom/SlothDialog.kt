package com.depromeet.sloth.presentation.custom

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.DialogSlothBinding

//TODO DialogFragment 로 변경? -> setOnClickListener 제거
class SlothDialog(context: Context, private val state: DialogState) {

    lateinit var onItemClickListener: OnItemClickedListener

    private lateinit var binding: DialogSlothBinding

    private val dlg = Dialog(context)

    interface OnItemClickedListener {
        fun onItemClicked()
    }

    fun show() = with(dlg) {
        binding = DialogSlothBinding.inflate(layoutInflater)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // custom view 영역 size 적용
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 외부 클릭시 창닫기 금지
        setCancelable(false)
        setContentView(binding.root)

        when (state) {
            DialogState.FORBIDDEN -> {
                with(binding) {
                    ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
                    tvDialogMessage.setText(R.string.forbidden_dialog_message)
                    btnDialogCancel.visibility = View.GONE
                    btnDialogCheck.background = AppCompatResources.getDrawable(
                        context, R.drawable.bg_rounded_sloth
                    )
                    btnDialogCheck.setOnClickListener {
                        onItemClickListener.onItemClicked()
                        dismiss()
                    }
                }
            }

            DialogState.LOGOUT -> {
                with(binding) {
                    tvDialogMessage.setText(R.string.logout_dialog_message)

                    btnDialogCheck.setOnClickListener {
                        onItemClickListener.onItemClicked()
                        dismiss()
                    }
                }
            }

            DialogState.WITHDRAW -> {
                with(binding) {
                    tvDialogMessage.setText(R.string.withdraw_dialog_message)

                    btnDialogCheck.setOnClickListener {
                        onItemClickListener.onItemClicked()
                        dismiss()
                    }
                }
            }

            DialogState.DELETE_LESSON -> {
                with(binding) {
                    tvDialogMessage.setText(R.string.delete_lesson_dialog_message)
                    btnDialogCheck.setText(R.string.delete_lesson_dialog_message_check)

                    btnDialogCheck.setOnClickListener {
                        onItemClickListener.onItemClicked()
                        dismiss()
                    }
                }
            }

            DialogState.WAIT -> {
                with(binding) {
                    ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
                    tvDialogMessage.setText(R.string.wait_dialog_message)
                    btnDialogCancel.visibility = View.GONE
                    btnDialogCheck.background = AppCompatResources.getDrawable(
                        context, R.drawable.bg_rounded_sloth
                    )

                    btnDialogCheck.setOnClickListener {
                        dismiss()
                    }
                }
            }

            DialogState.COMPLETE -> {
                with(binding) {
                    tvDialogMessage.setText(R.string.check_all_lesson_finished)
                    ivDialogState.setColorFilter(ContextCompat.getColor(context, R.color.sloth))
                    btnDialogCheck.apply {
                        backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sloth))
                        text = context.getString(R.string.finished)
                        setOnClickListener {
                            onItemClickListener.onItemClicked()
                            dismiss()
                        }
                    }
                }
            }
        }

        binding.btnDialogCancel.setOnClickListener {
            dismiss()
        }

        show()
    }
}

enum class DialogState {
    FORBIDDEN, LOGOUT, WITHDRAW, DELETE_LESSON, WAIT, COMPLETE
}