package com.depromeet.sloth.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.databinding.DialogLoadingBinding

abstract class BaseActivity<B: ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {
    protected lateinit var binding: B

    private val loadingDialog: AppCompatDialog by lazy {
        DialogLoadingBinding.inflate(LayoutInflater.from(this), null, false)
            .run {
                AppCompatDialog(this@BaseActivity)
                    .apply {
                        setCancelable(false)
                        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                        setContentView(this@run.root)
                    }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }

    open fun initViews() = Unit

    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun mainScope(block: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            block.invoke()
        }
    }

    protected inline fun bind(block: B.() -> Unit) {
        binding.apply(block)
    }

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
}
