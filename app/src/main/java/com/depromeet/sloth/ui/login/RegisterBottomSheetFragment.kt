package com.depromeet.sloth.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.depromeet.sloth.databinding.FragmentRegisterBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RegisterBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRegisterBottomBinding
    private var _binding: FragmentRegisterBottomBinding? = null

    private lateinit var registerListener: RegisterListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBottomBinding.inflate(inflater, container, false)
        binding = _binding!!

        binding.tvLoginPolicySloth.setOnClickListener {
            openSlothPolicy()
        }

        binding.btnLoginPolicyCancel.setOnClickListener {
            registerListener.onCancel()
        }

        binding.btnLoginPolicyAgree.setOnClickListener {
            registerListener.onAgree()
        }

        return binding.root
    }

    private fun openSlothPolicy() {
        //정책 WebView로 보여주는 로직
    }

    fun setRegisterListener(registerListener: RegisterListener) {
        this.registerListener = registerListener
    }

    companion object {
        const val TAG = "RegisterBottomSheetFragment"
    }
}