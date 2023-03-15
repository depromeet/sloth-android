package com.depromeet.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentOnBoardingCheckDetailDialogBinding


class OnBoardingCheckDetailDialogFragment :
    OnBoardingDialogFragment<FragmentOnBoardingCheckDetailDialogBinding>(R.layout.fragment_on_boarding_check_detail_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    override fun getTheme(): Int = R.style.OnBoardingCheckDetailDialog

    private fun initListener() {
        binding.root.setOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }
}