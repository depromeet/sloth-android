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
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.member.MemberLogoutState
import com.depromeet.sloth.data.network.member.MemberState
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.data.network.member.MemberUpdateState
import com.depromeet.sloth.data.network.notification.NotificationState
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.focusInputForm
import com.depromeet.sloth.extensions.hideKeyBoard
import com.depromeet.sloth.extensions.logout
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.extensions.showWithdrawalDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.login.SlothPolicyWebViewActivity
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val viewModel: ManageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
            // 상태가 바로 반영 되지 않음
            // member = viewModel.memberInfo.value.successOrNull()
            // member = viewModel.member.value
        }
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            memberState.observe(viewLifecycleOwner) { memberState ->
                when (memberState) {
                    is MemberState.Loading -> showProgress()

                    is MemberState.Success<Member> -> {
                        Timber.tag("fetch Success").d("${memberState.data}")
                        viewModel.setMemberInfo(memberState.data)
                    }

                    is MemberState.Unauthorized -> {
                        showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                    }

                    is MemberState.Error -> {
                        Timber.tag("fetch Error").d(memberState.exception)
                        showToast(getString(R.string.member_info_fetch_fail))
                    }
                }
                hideProgress()
            }

            repeatOnStarted {
                memberUpdateState.collect { memberUpdateState ->
                    when (memberUpdateState) {
                        is MemberUpdateState.Loading -> showProgress()

                        is MemberUpdateState.Success<MemberUpdateResponse> -> {
                            Timber.tag("update Success").d("${memberUpdateState.data}")
                            showToast(getString(R.string.member_update_success))
                            viewModel.fetchMemberInfo()
                        }

                        is MemberUpdateState.Unauthorized -> {
                            showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                        }

                        is MemberUpdateState.Error -> {
                            Timber.tag("update Error").d(memberUpdateState.exception)
                            showToast(getString(R.string.member_update_fail))
                        }
                    }
                    hideProgress()
                }
            }

            repeatOnStarted {
                notificationReceiveState.collect { notificationState ->
                    when (notificationState) {
                        is NotificationState.Loading -> showProgress()

                        is NotificationState.Success<String> -> {
                            showToast(getString(R.string.noti_update_complete))
                            viewModel.fetchMemberInfo()
                        }

                        is NotificationState.Unauthorized -> {
                            showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                        }

                        is NotificationState.Error -> {
                            Timber.tag("update Error").d(notificationState.exception)
                            showToast(getString(R.string.noti_update_fail))
                        }
                    }
                }
            }

            memberLogoutState.observe(viewLifecycleOwner) { memberLogoutState ->
                when (memberLogoutState) {
                    is MemberLogoutState.Loading -> showProgress()

                    is MemberLogoutState.Success<String> -> logout(requireContext()) { viewModel.removeAuthToken() }

                    is MemberLogoutState.Unauthorized -> {
                        showLogoutDialog(requireContext()) { viewModel.removeAuthToken() }
                    }

                    is MemberLogoutState.Error -> {
                        Timber.tag("update Error").d(memberLogoutState.exception)
                        showToast(getString(R.string.logout_fail))
                    }
                }
                hideProgress()
            }

            member.observe(viewLifecycleOwner) { member ->
                binding.member = member
            }

            repeatOnStarted {
                profileClick.collect { showProfileUpdateDialog() }
            }

            repeatOnStarted {
                contactButtonClick.collect { sendEmail() }
            }

            repeatOnStarted {
                privatePolicyClick.collect {
                    startActivity(Intent(requireContext(), SlothPolicyWebViewActivity::class.java))
                }
            }

            repeatOnStarted {
                logoutClick.collect {
                    val dlg = SlothDialog(requireContext(), DialogState.LOGOUT)
                    dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                        override fun onItemClicked() {
                            viewModel.logout()
                        }
                    }
                    dlg.start()
                }
            }

            repeatOnStarted {
                withdrawalClick.collect {
                    showWithdrawalDialog(requireContext()) { viewModel.removeAuthToken() }
                }
            }
        }

//        collectLatestLifecycleFlow(viewModel.memberInfo) { memberInfo ->
//            Timber.d("${memberInfo.successOrNull()}")
//            binding.member = memberInfo.successOrNull()
//        }
    }

    private fun showProfileUpdateDialog() {
        val updateDialog = Dialog(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
        updateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        updateDialog.setContentView(R.layout.dialog_manage_update_member_info)

        val nameEditText =
            updateDialog.findViewById<EditText>(R.id.et_manage_dialog_profile_name)
        val updateButton =
            updateDialog.findViewById<AppCompatButton>(R.id.btn_manage_dialog_update_member_info)

        nameEditText.hint = viewModel.member.value?.memberName ?: ""
        //nameEditText.hint = viewModel.memberInfo.value.successOrNull()?.memberName ?: ""
        focusInputForm(nameEditText, updateButton, requireContext())

        updateButton.setOnClickListener {
            if (nameEditText.text.toString() != (viewModel.member.value?.memberName ?: "")) {
//            if (nameEditText.text.toString() != (viewModel.memberInfo.value.successOrNull()?.memberName ?: "")) {
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