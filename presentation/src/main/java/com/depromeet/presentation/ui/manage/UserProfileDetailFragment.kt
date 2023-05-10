package com.depromeet.presentation.ui.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentUserProfileDetailBinding
import com.depromeet.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserProfileDetailFragment : BaseFragment<FragmentUserProfileDetailBinding>(R.layout.fragment_user_profile_detail) {

    private val viewModel: UserProfileDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }

        initListener()
    }

    private fun initListener() {
        binding.tbUserProfileDetail.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }
}