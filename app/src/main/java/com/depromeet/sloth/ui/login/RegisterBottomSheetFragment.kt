package com.depromeet.sloth.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.depromeet.sloth.databinding.FragmentRegisterBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.content.Intent

class RegisterBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRegisterBottomBinding
    private var _binding: FragmentRegisterBottomBinding? = null

    private lateinit var registerListener: RegisterListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    //TODO 로그인 화면 까지 네비게이션에 포함시켜야 진정으로 웹뷰 액티비티를 제거할 수 있음
    private fun openSlothPolicy() {
        startActivity(
            Intent(requireContext(), SlothPolicyWebViewActivity::class.java)
        )
    }

    fun setRegisterListener(registerListener: RegisterListener) {
        this.registerListener = registerListener
    }

    companion object {
        const val TAG = "RegisterBottomSheetFragment"
    }
}