package com.depromeet.presentation.ui.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.NavMainDirections
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentLogoutDialogBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.ui.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LogoutDialogFragment : BaseDialogFragment<FragmentLogoutDialogBinding>(R.layout.fragment_logout_dialog) {

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
                viewModel.logoutCancelEvent.collect {
                        closeLogoutDialog()
                    }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun closeLogoutDialog() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

    private fun navigateToLogin() {
//        val navController = findNavController()
//        val backStack = navController.backQueue
//        val bottomBackStackEntry = backStack[1] // 최하단 BackStackEntry
//        val popUpToFragmentId = bottomBackStackEntry.destination.id // 최하단 Fragment의 ID
//        val popUpToFragmentLabel = bottomBackStackEntry.destination.label
        val action = NavMainDirections.actionGlobalToLogin()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(findNavController().backQueue[1].destination.id, true)
            .build()
        findNavController().navigate(action, navOptions)
    }
}