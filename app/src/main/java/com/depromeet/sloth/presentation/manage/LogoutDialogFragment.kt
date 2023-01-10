package com.depromeet.sloth.presentation.manage

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentLogoutDialogBinding
import com.depromeet.sloth.extensions.logout
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LogoutDialogFragment :
    BaseDialogFragment<FragmentLogoutDialogBinding>(R.layout.fragment_logout_dialog) {

    private val manageViewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = manageViewModel
        }
        initObserver()
    }

    private fun initObserver() = with(manageViewModel) {
        repeatOnStarted {
            launch {
                launch {
                    logoutSuccess
                        .collect {
                            logout(
                                requireContext(),
                                this@LogoutDialogFragment
                            ) { deleteAuthToken() }
                        }
                }

                launch {
                    logoutCancel
                        .collect {
                            closeLogoutDialog()
                        }
                }
            }
        }
    }

    private fun closeLogoutDialog() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

}