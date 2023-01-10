//package com.depromeet.sloth.presentation.custom
//
//import android.app.Dialog
//import android.content.Context
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.view.View
//import android.view.Window
//import androidx.core.content.ContextCompat
//import com.depromeet.sloth.R
//import com.depromeet.sloth.databinding.DialogSlothBinding
//
//class SlothDialog(context: Context, private val state: DialogState) {
//
//    lateinit var onItemClickListener: OnItemClickedListener
//
//    private lateinit var binding: DialogSlothBinding
//
//    private val dlg = Dialog(context)
//
//    interface OnItemClickedListener {
//        fun onItemClicked()
//    }
//
//    fun show() = with(dlg) {
//        binding = DialogSlothBinding.inflate(layoutInflater)
//
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        // custom view 영역 size 적용
//        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        // 외부 클릭시 창닫기 금지
//        setCancelable(false)
//        setContentView(binding.root)
//
//        when (state) {
//            DialogState.FORBIDDEN -> {
//                with(binding) {
//                    ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
//
//                    tvDialogMessage.setText(R.string.expired_dialog_message)
//
//                    btnDialogCancel.visibility = View.GONE
//
//                    btnDialogCheck.apply {
//                        setBackgroundColor(context.getColor(R.color.sloth))
//                        setOnClickListener {
//                            onItemClickListener.onItemClicked()
//                            dismiss()
//                        }
//                    }
//                }
//            }
//
//            DialogState.LOGOUT -> {
//                with(binding) {
//                    tvDialogMessage.setText(R.string.logout_dialog_message)
//
//                    btnDialogCheck.setOnClickListener {
//                        onItemClickListener.onItemClicked()
//                        dismiss()
//                    }
//                }
//            }
//
//            DialogState.WITHDRAW -> {
//                with(binding) {
//                    tvDialogMessage.setText(R.string.withdraw_dialog_message)
//
//                    btnDialogCheck.setOnClickListener {
//                        onItemClickListener.onItemClicked()
//                        dismiss()
//                    }
//                }
//            }
//
//            DialogState.DELETE_LESSON -> {
//                with(binding) {
//                    tvDialogMessage.setText(R.string.delete_lesson_dialog_message)
//
//                    btnDialogCheck.apply {
//                        setText(R.string.delete_lesson_dialog_check)
//                        setOnClickListener {
//                            onItemClickListener.onItemClicked()
//                            dismiss()
//                        }
//                    }
//                }
//            }
//
//            DialogState.WAIT -> {
//                with(binding) {
//                    ivDialogState.setImageResource(R.mipmap.ic_sloth_logo)
//
//                    tvDialogMessage.setText(R.string.wait_dialog_message)
//
//                    btnDialogCancel.visibility = View.GONE
//
//                    btnDialogCheck.apply {
//                        setBackgroundColor(context.getColor(R.color.sloth))
//                        setOnClickListener {
//                            dismiss()
//                        }
//                    }
//                }
//            }
//
//            DialogState.COMPLETE -> {
//                with(binding) {
//                    ivDialogState.setColorFilter(ContextCompat.getColor(context, R.color.sloth))
//
//                    tvDialogMessage.setText(R.string.finish_lesson_dialog_message)
//
//                    btnDialogCheck.apply {
//                        setBackgroundColor(context.getColor(R.color.sloth))
//                        text = context.getString(R.string.finish_lesson_dialog_check)
//                        setOnClickListener {
//                            onItemClickListener.onItemClicked()
//                            dismiss()
//                        }
//                    }
//                }
//            }
//        }
//
//        binding.btnDialogCancel.setOnClickListener {
//            dismiss()
//        }
//
//        show()
//    }
//}
//
//enum class DialogState {
//    FORBIDDEN, LOGOUT, WITHDRAW, DELETE_LESSON, WAIT, COMPLETE
//}