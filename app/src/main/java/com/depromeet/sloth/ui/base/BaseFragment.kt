package com.depromeet.sloth.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.util.LoadingDialogUtil

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int,
) : Fragment() {

    private var _binding: B? = null
    protected val binding
        get() = _binding!!

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

    open fun observeData() = Unit

    protected fun showToast(msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    protected fun showProgress() {
        LoadingDialogUtil.showProgress(requireActivity())
    }

    protected fun hideProgress() {
        LoadingDialogUtil.hideProgress()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}