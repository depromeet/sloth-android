package com.depromeet.sloth.ui.manage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.request.member.MemberUpdateRequest
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.data.model.response.member.MemberUpdateResponse
import com.depromeet.sloth.databinding.DialogManageUpdateMemberInfoBinding
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.*
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.login.SlothPolicyWebViewActivity
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO 화면 전환 시 푸시알림 수신버튼이 이동되는 애니메이션이 보이는 현상 제거
@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val manageViewModel: ManageViewModel by viewModels()

    lateinit var dialogBinding: DialogManageUpdateMemberInfoBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = manageViewModel
        }
        dialogBinding = DialogManageUpdateMemberInfoBinding.inflate(layoutInflater)
        initObserver()
    }

    private fun initObserver() = with(manageViewModel) {
        repeatOnStarted {
            launch {
                memberState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<MemberResponse> -> manageViewModel.setMemberInfo(
                                result.data
                            )
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                        manageViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(getString(R.string.member_info_fetch_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                memberUpdateState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<MemberUpdateResponse> -> {
                                showToast(getString(R.string.member_update_success))
                                setMemberName(result.data.memberName)
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                        manageViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Update Error").d(result.throwable)
                                        showToast(getString(R.string.member_update_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                notificationReceiveState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> {
                                showToast(getString(R.string.noti_update_complete))
                                setMemberNotificationReceive(binding.scManageNotificationStatus.isChecked)
                            }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                        manageViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Update Error").d(result.throwable)
                                        showToast(getString(R.string.noti_update_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                memberLogoutState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> logout(requireContext()) { manageViewModel.removeAuthToken() }
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(requireContext()) {
                                        manageViewModel.removeAuthToken()
                                    }
                                    else -> {
                                        Timber.tag("Logout Error").d(result.throwable)
                                        showToast(getString(R.string.logout_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                profileClick
                    .collect {
                        showProfileUpdateDialog()
                    }
            }

            launch {
                contactButtonClick
                    .collect {
                        sendEmail()
                    }
            }

            launch {
                privatePolicyClick
                    .collect {
                        startActivity(
                            Intent(
                                requireContext(),
                                SlothPolicyWebViewActivity::class.java
                            )
                        )
                    }
            }

            launch {
                logoutClick
                    .collect {
                        showLogoutDialog()
                    }
            }

            launch {
                withdrawalClick
                    .collect {
                        showWithdrawalDialog(requireContext()) { manageViewModel.removeAuthToken() }
                    }
            }
        }
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

    //TODO Dialog 에 Databinding 적용
    private fun showProfileUpdateDialog() = with(dialogBinding) {
        val updateDialog = Dialog(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(dialogBinding.root)
            }

        etManageProfileName.hint = manageViewModel.memberName.value
        focusInputForm(etManageProfileName, btnManageUpdateMemberInfo, requireContext())

        btnManageUpdateMemberInfo.setOnClickListener {
            if (etManageProfileName.text.toString() != manageViewModel.memberName.value) {
                manageViewModel.updateMemberInfo(MemberUpdateRequest(memberName = etManageProfileName.text.toString()))
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