package com.depromeet.sloth.presentation.screen.onboarding

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.depromeet.sloth.R


abstract class OnBoardingDialogFragment<B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int
) : DialogFragment() {

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
        setDialogSize()
    }

    override fun getTheme(): Int = R.style.OnBoardingDialog

    open fun setDialogSize() {
        context?.let {
            dialog?.let { dialog ->
                dialog.window?.let { window ->
                    val params: ViewGroup.LayoutParams = window.attributes
                    params.width = WindowManager.LayoutParams.MATCH_PARENT
                    params.height = WindowManager.LayoutParams.MATCH_PARENT
                    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    protected inline fun bind(block: B.() -> Unit) {
        binding.apply(block)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}