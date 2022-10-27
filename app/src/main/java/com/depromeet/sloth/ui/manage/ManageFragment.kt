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
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import com.depromeet.sloth.data.network.member.MemberUpdateInfoResponse
import com.depromeet.sloth.data.network.member.MemberUpdateState
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.focusInputForm
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.extensions.hideKeyBoard
import com.depromeet.sloth.extensions.logout
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.extensions.showWithdrawalDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.common.EventObserver
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
            //member = viewModel.memberInfo.value.successOrNull()
            vm = viewModel
        }

        initObserver()
        initViews()
    }

    private fun initObserver() {
        viewModel.apply {
            memberState.observe(viewLifecycleOwner) { memberState ->
                when (memberState) {
                    is MemberState.Loading -> handleLoadingState(requireContext())

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

            memberUpdateState.observe(viewLifecycleOwner, EventObserver { memberUpdateState ->
                when (memberUpdateState) {
                    is MemberUpdateState.Loading -> handleLoadingState(requireContext())

                    is MemberUpdateState.Success<MemberUpdateInfoResponse> -> {
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
            })

            memberLogoutState.observe(viewLifecycleOwner) { memberLogoutState ->
                when (memberLogoutState) {
                    is MemberLogoutState.Loading -> handleLoadingState(requireContext())

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
        }

//        collectLatestLifecycleFlow(viewModel.memberInfo) { memberInfo ->
//            Timber.d("${memberInfo.successOrNull()}")
//            binding.member = memberInfo.successOrNull()
//        }
    }

    override fun initViews() = with(binding) {
        ivManageProfileImage.setOnClickListener {
            showUpdateDialog()
        }

        clManageContact.setOnClickListener {
            sendEmail()
        }

        clManagePrivacy.setOnClickListener {
            startActivity(
                SlothPolicyWebViewActivity.newIntent(requireContext())
            )
        }

        clManageLogout.setOnClickListener {
            val dlg = SlothDialog(requireContext(), DialogState.LOGOUT)
            dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                override fun onItemClicked() {
                    viewModel.logout()
                }
            }
            dlg.start()
        }

        clManageWithdrawal.setOnClickListener {
            showWithdrawalDialog(requireContext()) { viewModel.removeAuthToken() }
        }

        scManage.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Timber.tag("scManage").d("true")
            } else {
                Timber.tag("scManage").d("false")
            }
        }
    }

    private fun showUpdateDialog() {
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
                viewModel.updateMemberInfo(MemberUpdateInfoRequest(nameEditText.text.toString()))
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