package com.depromeet.sloth.presentation.ui.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentExpiredDialogBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.presentation.ui.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ExpiredDialogFragment :
    BaseDialogFragment<FragmentExpiredDialogBinding>(R.layout.fragment_expired_dialog) {

    private val viewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initObserver()
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.logoutSuccessEvent.collect {
                        navigateToLogin()
                    }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_global_to_login)
    }
}