package com.depromeet.sloth.presentation.screen.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<B: ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {

    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preload()

        binding =  DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this@BaseActivity

        init()
    }

    abstract fun preload()

    abstract fun init()

    protected inline fun bind(block: B.() -> Unit) {
        binding.apply(block)
    }
}
