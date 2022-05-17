package com.depromeet.sloth.ui.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope

abstract class BaseActivity<B: ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }

    open fun initViews() = Unit

    open fun observeData() = Unit

    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun mainScope(block: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            block.invoke()
        }
    }
}
