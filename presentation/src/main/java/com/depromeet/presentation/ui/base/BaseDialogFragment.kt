package com.depromeet.presentation.ui.base

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.DialogLoadingBinding
import com.depromeet.presentation.extensions.dp


abstract class BaseDialogFragment<B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int
) : DialogFragment() {

    private var _binding: B? = null
    protected val binding
        get() = _binding!!

    private val loadingDialog: AppCompatDialog by lazy {
        DialogLoadingBinding.inflate(layoutInflater, null, false)
            .run {
                AppCompatDialog(requireContext())
                    .apply {
                        // system back 버튼 눌렀을 때 no cancle
                        setCancelable(false)
                        // Dialog 자체 배경 부분 투명 처리
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        // Dialog 호출 시 배경 화면이 검정색으로 바뀌는 것을 막기
                        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        setContentView(this@run.root)
                    }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        setSizeDialog()
    }

    override fun getTheme(): Int = R.style.SlothDialog

    private fun setSizeDialog() {
        context?.let {
            dialog?.let { dialog ->
                dialog.window?.let { window ->

                    val windowManager: WindowManager =
                        activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val size = Point()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val displayMetrics = windowManager.currentWindowMetrics
                        size.x = displayMetrics.bounds.width()
                        size.y = displayMetrics.bounds.height()
                    } else {
                        @Suppress("DEPRECATION")
                        val display = windowManager.defaultDisplay
                        display.getSize(size)
                    }

                    val params: ViewGroup.LayoutParams = window.attributes
                    // params.width = size.x - 64.ToPx
                    params.width = size.x - 64.dp
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    window.setGravity(Gravity.CENTER)
                    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

//    private fun Context.setSizeDialog() {
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        if(Build.VERSION.SDK_INT < 30){
//            val display = windowManager.defaultDisplay
//            val size = Point()
//
//            display.getSize(size)
//
//            val window = this@BaseDialogFragment.dialog?.window
//
//            val x = (size.x * widthPercent).toInt()
//            val y = (size.y * heightPercent).toInt()
//            window?.setLayout(x,y)
//        }else{
//            val rect = windowManager.currentWindowMetrics.bounds
//
//            val window = this@BaseDialogFragment.dialog?.window
//
//            val x = (rect.width() * widthPercent).toInt()
//            val y = (rect.height() * heightPercent).toInt()
//
//            window?.setLayout(x,y)
//        }
//    }

    protected inline fun bind(block: B.() -> Unit) {
        binding.apply(block)
    }

    open fun initViews() = Unit

    fun showProgress() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    fun hideProgress() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    fun showExpireDialog() {
        findNavController().navigate(R.id.action_global_to_expired_dialog)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}