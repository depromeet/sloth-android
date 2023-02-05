package com.depromeet.sloth.presentation.screen.manage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO 화면 전환 시 푸시알림 수신버튼이 비활성화 -> 활성화되는 애니메이션이 보이는 현상 제거
@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val manageViewModel: ManageViewModel by viewModels()

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
                navigateToUpdateMemberDialogEvent
                    .collect {
                        showUpdateMemberDialog()
                    }
            }

            launch {
                navigateToContactEvent
                    .collect {
                        sendEmail()
                    }
            }

            launch {
                navigateToPrivatePolicyEvent
                    .collect {
                        showPrivatePolicy()
                    }
            }

            launch {
                navigateToLogoutDialogEvent
                    .collect {
                        showLogoutDialog()
                    }
            }

            launch {
                navigateToWithdrawalDialogEvent
                    .collect {
                        showWithdrawalDialog()
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
                navigateToExpireDialogEvent
                    .collect {
                        showExpireDialog()
                    }
            }

            launch {
                showToastEvent
                    .collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun showUpdateMemberDialog() {
        val action = ManageFragmentDirections.actionManageToUpdateMemberDialog(
            manageViewModel.uiState.value.memberName
        )
        findNavController().safeNavigate(action)
    }

    private fun showPrivatePolicy() {
        val action = ManageFragmentDirections.actionManageToSlothPolicyWebview()
        findNavController().safeNavigate(action)
    }

    private fun showLogoutDialog() {
        val action = ManageFragmentDirections.actionManageToLogoutDialog()
        findNavController().safeNavigate(action)
    }

    // 회원 탈퇴 API 필요
    private fun showWithdrawalDialog() = Unit

    private fun sendEmail() {
        startActivity(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.sloth_official_mail)))
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