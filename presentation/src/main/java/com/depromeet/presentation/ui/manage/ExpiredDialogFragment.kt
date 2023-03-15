package com.depromeet.presentation.ui.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.NavMainDirections
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentExpiredDialogBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.ui.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ExpiredDialogFragment :
    BaseDialogFragment<FragmentExpiredDialogBinding>(R.layout.fragment_expired_dialog) {

    private val viewModel: ExpiredViewModel by viewModels()

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
                viewModel.navigateToLoginEvent.collect {
                        navigateToLogin()
                    }
            }
        }
    }

    private fun navigateToLogin() {
        val action = NavMainDirections.actionGlobalToLogin()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(findNavController().backQueue[1].destination.id, true)
            .build()
        findNavController().navigate(action, navOptions)
    }
}