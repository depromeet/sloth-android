package com.depromeet.sloth.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.databinding.DialogLoadingBinding

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int,
) : Fragment() {

    private var _binding: B? = null
    protected val binding
        get() = _binding!!

    private val loadingDialog: AppCompatDialog by lazy {
        DialogLoadingBinding.inflate(LayoutInflater.from(requireContext()), null, false)
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
    }

    fun mainScope(block: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            block.invoke()
        }
    }

    protected inline fun bind(block: B.() -> Unit) {
        binding.apply(block)
    }

    open fun initViews() = Unit

    protected fun showToast(msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

//    protected fun showProgress() {
//        LoadingDialogUtil.showProgress(requireActivity())
//    }
//
//    protected fun hideProgress() {
//        LoadingDialogUtil.hideProgress()
//    }

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