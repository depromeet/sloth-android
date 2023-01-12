package com.depromeet.sloth.presentation.screen.lessonlist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentWaitDialogBinding
import com.depromeet.sloth.presentation.screen.base.BaseDialogFragment


class WaitDialogFragment : BaseDialogFragment<FragmentWaitDialogBinding>(R.layout.fragment_wait_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO 데이터바인딩 ClickListener 로 변경
        binding.btnDialogCheck.setOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }
}