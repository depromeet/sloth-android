package com.depromeet.sloth.presentation.screen.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.DialogLoadingBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int,
) : BottomSheetDialogFragment() {

    private var _binding: B? = null
    protected val binding
        get() = _binding!!

    private val loadingDialog: AppCompatDialog by lazy {
        DialogLoadingBinding.inflate(LayoutInflater.from(requireContext()), null, false)
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
    }

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

    override fun getTheme(): Int = R.style.SlothBottomSheetDialog

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}