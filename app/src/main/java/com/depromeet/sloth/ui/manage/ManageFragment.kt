package com.depromeet.sloth.ui.manage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.member.*
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.focusInputForm
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.login.SlothPolicyWebViewActivity
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>() {

    private val viewModel: ManageViewModel by activityViewModels()

    lateinit var memberName: String

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentManageBinding =
        FragmentManageBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            memberState.observe(viewLifecycleOwner) { memberState ->
                when (memberState) {
                    is MemberState.Loading -> handleLoadingState()

                    is MemberState.Success<MemberInfoResponse> -> handleSuccessState(memberState.data)

                    is MemberState.Unauthorized -> showLogoutDialog()

                    is MemberState.NotFound, MemberState.Forbidden -> {
                        Toast.makeText(requireContext(), "회원 정보를 가져오지 못했어요", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is MemberState.Error -> {
                        Log.d("fetch Error", "${memberState.exception}")
                        Toast.makeText(requireContext(), "회원 정보를 가져오지 못했어요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            memberUpdateState.observe(viewLifecycleOwner) { memberUpdateState ->
                when (memberUpdateState) {
                    is MemberUpdateState.Loading -> handleLoadingState()

                    is MemberUpdateState.Success<MemberUpdateInfoResponse> -> handleSuccessState(
                        memberUpdateState.data)

                    is MemberUpdateState.NoContent, MemberUpdateState.Forbidden ->
                        Toast.makeText(requireContext(), "회원 정보를 가져오지 못했어요", Toast.LENGTH_SHORT)
                            .show()

                    is MemberUpdateState.Unauthorized -> showLogoutDialog()

                    is MemberUpdateState.Error -> {
                        Log.d("update Error", "${memberUpdateState.exception}")
                        Toast.makeText(requireContext(), "회원 정보를 가져오지 못했어요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            memberLogoutState.observe(viewLifecycleOwner) { memberLogoutState ->
                when (memberLogoutState) {
                    is MemberLogoutState.Loading -> handleLoadingState()

                    is MemberLogoutState.Success<String> -> handleSuccessState(memberLogoutState.data)

                    is MemberLogoutState.Created -> Unit

                    is MemberLogoutState.Unauthorized -> showLogoutDialog()

                    is MemberLogoutState.Forbidden, MemberLogoutState.NotFound ->
                        Toast.makeText(requireContext(), "회원 정보를 가져오지 못했어요", Toast.LENGTH_SHORT)
                            .show()

                    is MemberLogoutState.Error -> {
                        Log.d("update Error", "${memberLogoutState.exception}")
                        Toast.makeText(requireContext(), "회원 정보를 가져오지 못했어요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        initViews()
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

        clManageWithdraw.setOnClickListener {
            showWithdrawDialog()
        }
    }

    private fun handleLoadingState() {
        showProgress(requireContext())
    }

    private fun <T> handleSuccessState(data: T) {
        if (data is MemberInfoResponse) {
            initMemberInfo(data)
        } else if (data is MemberUpdateInfoResponse) {
            updateMemberInfo(data)
        } else {
            logout()
        }
        hideProgress()
    }

    private fun showLogoutDialog() {
        val dlg = SlothDialog(requireContext(), DialogState.FORBIDDEN)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                logout()
            }
        }
        dlg.start()
    }

    private fun logout() {
        viewModel.removeAuthToken()
        Toast.makeText(requireContext(), "로그아웃 되었어요", Toast.LENGTH_SHORT).show()
        startActivity(LoginActivity.newIntent(requireActivity()))
    }


    private fun showWithdrawDialog() {
        val dlg = SlothDialog(requireContext(), DialogState.WITHDRAW)
        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
            override fun onItemClicked() {
                withdraw()
            }
        }
        dlg.start()
    }

    //회원 탈퇴 api 필요
    private fun withdraw() {
        viewModel.removeAuthToken()
        Toast.makeText(requireContext(), "회원탈퇴 되었어요", Toast.LENGTH_SHORT).show()
        startActivity(LoginActivity.newIntent(requireActivity()))
    }

    private fun initMemberInfo(data: MemberInfoResponse) = with(binding) {
        memberName = data.memberName
        tvManageProfileName.text = memberName
        tvManageProfileEmail.text = data.email
    }

    private fun showUpdateDialog() {
        val updateDialog = Dialog(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
        updateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        updateDialog.setContentView(R.layout.dialog_manage_update_member_info)

        val nameEditText =
            updateDialog.findViewById<EditText>(R.id.et_manage_dialog_profile_name)
        val updateButton =
            updateDialog.findViewById<AppCompatButton>(R.id.btn_manage_dialog_update_member_info)

        nameEditText.hint = memberName
        focusInputForm(nameEditText, updateButton, requireContext())

        updateButton.setOnClickListener {
            if (nameEditText.text.toString() != memberName) {
                viewModel.updateMemberInfo(MemberUpdateInfoRequest(memberName = nameEditText.text.toString()))
            } else {
                Toast.makeText(requireContext(), "현재 닉네임과 동일한 닉네임이에요", Toast.LENGTH_SHORT).show()
            }
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    private fun updateMemberInfo(memberUpdateInfoResponse: MemberUpdateInfoResponse) =
        with(binding) {
            tvManageProfileName.text = memberUpdateInfoResponse.memberName
            //Toast.makeText(requireContext(), "닉네임이 변경되었어요", Toast.LENGTH_SHORT).show()
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