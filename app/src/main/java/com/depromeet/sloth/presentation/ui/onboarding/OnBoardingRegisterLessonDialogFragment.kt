package com.depromeet.sloth.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentOnBoardingRegisterLessonDialogBinding


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