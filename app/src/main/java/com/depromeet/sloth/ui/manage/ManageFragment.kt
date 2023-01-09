package com.depromeet.sloth.ui.manage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.logout
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showToast
import com.depromeet.sloth.extensions.showWithdrawalDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO 화면 전환 시 푸시알림 수신버튼이 이동되는 애니메이션이 보이는 현상 제거
@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val manageViewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_home)

    override fun onStart() {
        super.onStart()
        manageViewModel.fetchMemberInfo()
    }

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
                logoutSuccess
                    .collect {
                        logout(requireContext(), this@ManageFragment) { removeAuthToken() }
                    }
            }

            launch {
                navigateToUpdateProfileDialogEvent
                    .collect { showProfileUpdateDialog() }
            }

            launch {
                navigateToContactEvent
                    .collect { sendEmail() }
            }

            launch {
                navigateToPrivatePolicyEvent
                    .collect { showPrivatePolicy() }
            }

            launch {
                navigateToLogoutDialogEvent
                    .collect { showLogoutDialog() }
            }

            launch {
                navigateToWithdrawalDialogEvent
                    .collect {
                        showWithdrawalDialog(
                            requireContext(),
                            this@ManageFragment
                        ) { removeAuthToken() }
                    }
            }

            launch {
                isLoading
                    .collect { isLoading ->
                        when (isLoading) {
                            true -> showProgress()
                            false -> hideProgress()
                        }
                    }
            }

            launch {
                internetError
                    .collect { error ->
                        when (error) {
                            true -> showNetworkError()
                            false -> closeNetworkError()
                        }
                    }
            }

            launch {
                showForbiddenDialogEvent
                    .collect {
                        showForbiddenDialog(
                            requireContext(),
                            this@ManageFragment
                        ) { removeAuthToken() }
                    }
            }

            launch {
                showToastEvent
                    .collect { message ->
                        showToast(requireContext(), message)
                    }
            }
        }
    }

    private fun showNetworkError() {
        binding.manageNetworkError.itemNetworkError.visibility = View.VISIBLE
    }

    private fun closeNetworkError() {
        binding.manageNetworkError.itemNetworkError.visibility = View.GONE
    }

    private fun showProfileUpdateDialog() {
        val action = ManageFragmentDirections.actionManageToUpdateMemberFragment()
        findNavController().safeNavigate(action)
    }

    private fun showPrivatePolicy() {
        val action = ManageFragmentDirections.actionManageToSlothPolicyWebview()
        findNavController().safeNavigate(action)
    }

    private fun showLogoutDialog() {
        val dlg = SlothDialog(requireContext(), DialogState.LOGOUT)
        dlg.onItemClickListener =
            object : SlothDialog.OnItemClickedListener {
                override fun onItemClicked() {
                    manageViewModel.logout()
                }
            }
        dlg.show()
    }

    private fun sendEmail() {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(getString(R.string.sloth_official_mail))
                )
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_subject))
                putExtra(
                    Intent.EXTRA_TEXT,
                    String.format(
                        CELLPHONE_INFO_DIVER,
                        BuildConfig.VERSION_NAME,
                        Build.VERSION.SDK_INT,
                        Build.VERSION.RELEASE,
                        Build.MODEL
                    )
                )
                type = MESSAGE_TYPE
            }
        )
    }
}