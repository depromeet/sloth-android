package com.depromeet.presentation.ui.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.NavMainDirections
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentWithdrawDialogBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.ui.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class WithdrawDialogFragment : BaseDialogFragment<FragmentWithdrawDialogBinding>(R.layout.fragment_withdraw_dialog) {

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
                viewModel.withdrawSuccessEvent.collect {
                    navigateToLogin()
                }
            }

            launch {
                viewModel.withdrawCancelEvent.collect {
                    closeWithdrawDialog()
                }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun closeWithdrawDialog() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

    private fun navigateToLogin() {
        val action = NavMainDirections.actionGlobalToLogin()
        val navOptions = NavOptions.Builder()
            //.setPopUpTo(findNavController().backQueue[1].destination.id, true)
            .setPopUpTo(findNavController().currentBackStack.value[1].destination.id, true)
            .build()
        findNavController().navigate(action, navOptions)
    }
}