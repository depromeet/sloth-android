package com.depromeet.sloth.presentation.base

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
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
import com.depromeet.sloth.databinding.DialogLoadingBinding
import com.depromeet.sloth.extensions.dp


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
                        setCancelable(false)
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

    private fun setSizeDialog() {
        context?.let {
            dialog?.let { dialog ->
                dialog.window?.let { window ->

                    val windowManager: WindowManager =
                        activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val display = windowManager.defaultDisplay
                    val size = Point()
                    display.getSize(size)

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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}