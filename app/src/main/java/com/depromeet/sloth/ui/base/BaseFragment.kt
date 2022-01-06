package com.depromeet.sloth.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.depromeet.sloth.util.LoadingDialogUtil
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    protected lateinit var binding: VB

    abstract fun getViewBinding(): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        return binding.root
    }

    fun mainScope(block: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            block.invoke()
        }
    }

    open fun initViews() = Unit

    open fun observeData() = Unit

    protected fun showProgress() {
        LoadingDialogUtil.showProgress(requireActivity())
    }

    protected fun hideProgress() {
        LoadingDialogUtil.hideProgress()
    }
}