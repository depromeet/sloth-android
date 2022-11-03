package com.depromeet.sloth.ui.manage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.UiState
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.login.SlothPolicyWebViewActivity
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val viewModel: ManageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                memberState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success<Member> -> viewModel.setMemberInfo(uiState.data)
                            is UiState.Unauthorized -> showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                            is UiState.Error -> {
                                Timber.tag("fetch Error").d(uiState.throwable)
                                showToast(getString(R.string.member_info_fetch_fail))
                            }
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                memberUpdateState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success<MemberUpdateResponse> -> {
                                showToast(getString(R.string.member_update_success))
                                viewModel.fetchMemberInfo()
                            }
                            is UiState.Unauthorized -> {
                                showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                            }
                            is UiState.Error -> {
                                Timber.tag("update Error").d(uiState.throwable)
                                showToast(getString(R.string.member_update_fail))
                            }
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                notificationReceiveState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success<String> -> {
                                showToast(getString(R.string.noti_update_complete))
                                viewModel.fetchMemberInfo()
                            }
                            is UiState.Unauthorized -> showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                            is UiState.Error -> {
                                Timber.tag("update Error").d(uiState.throwable)
                                showToast(getString(R.string.noti_update_fail))
                            }
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                memberLogoutState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> showProgress()
                            is UiState.Success<String> -> logout(requireContext()) { viewModel.removeAuthToken() }
                            is UiState.Unauthorized -> showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                            is UiState.Error -> {
                                Timber.tag("logout Error").d(uiState.throwable)
                                showToast(getString(R.string.logout_fail))
                            }
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                member
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { member -> binding.member = member }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                profileClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { showProfileUpdateDialog() }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                contactButtonClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { sendEmail() }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                privatePolicyClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        startActivity(
                            Intent(
                                requireContext(),
                                SlothPolicyWebViewActivity::class.java
                            )
                        )
                    }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                logoutClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect { showLogoutDialog() }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                withdrawalClick
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        showWithdrawalDialog(requireContext()) { viewModel.removeAuthToken() }
                    }
            }
        }
    }

    private fun showLogoutDialog() {
        val dlg = SlothDialog(requireContext(), DialogState.LOGOUT)
        dlg.onItemClickListener =
            object : SlothDialog.OnItemClickedListener {
                override fun onItemClicked() {
                    viewModel.logout()
                }
            }
        dlg.start()
    }

    private fun showProfileUpdateDialog() {
        val updateDialog =
            Dialog(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
        updateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        updateDialog.setContentView(R.layout.dialog_manage_update_member_info)

        val nameEditText =
            updateDialog.findViewById<EditText>(R.id.et_manage_dialog_profile_name)
        val updateButton =
            updateDialog.findViewById<AppCompatButton>(R.id.btn_manage_dialog_update_member_info)

        nameEditText.hint = viewModel.member.value.memberName ?: ""
        focusInputForm(nameEditText, updateButton, requireContext())

        updateButton.setOnClickListener {
            if (nameEditText.text.toString() != (viewModel.member.value.memberName ?: "")) {
                viewModel.updateMemberInfo(MemberUpdateRequest(memberName = nameEditText.text.toString()))
            } else {
                hideKeyBoard(requireActivity())
                showToast(getString(R.string.input_same_nickname))
            }
            updateDialog.dismiss()
        }
        updateDialog.show()
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