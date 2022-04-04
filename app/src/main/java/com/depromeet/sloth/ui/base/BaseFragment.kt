package com.depromeet.sloth.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.depromeet.sloth.util.LoadingDialogUtil

abstract class BaseFragment<B : ViewBinding>: Fragment() {

    private var _binding: B? =  null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentBinding(layoutInflater, container)
        return binding.root
    }

    fun mainScope(block: suspend () -> Unit) {
        lifecycleScope.launchWhenCreated {
            block.invoke()
        }
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    open fun initViews() = Unit

    open fun observeData() = Unit

    protected fun showProgress() {
        LoadingDialogUtil.showProgress(requireActivity())
    }

    protected fun hideProgress() {
        LoadingDialogUtil.hideProgress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}