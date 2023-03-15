package com.depromeet.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentOnBoardingRegisterLessonDialogBinding


class OnBoardingRegisterLessonDialogFragment :
    OnBoardingDialogFragment<FragmentOnBoardingRegisterLessonDialogBinding>(
        R.layout.fragment_on_boarding_register_lesson_dialog
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.root.setOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }
}