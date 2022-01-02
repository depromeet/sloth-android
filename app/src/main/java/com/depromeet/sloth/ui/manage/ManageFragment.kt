package com.depromeet.sloth.ui.manage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatButton
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.member.MemberInfoResponse
import com.depromeet.sloth.data.network.member.MemberState
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import com.depromeet.sloth.data.network.member.MemberUpdateInfoResponse
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.login.SlothPolicyWebViewActivity

class ManageFragment : BaseFragment<ManageViewModel, FragmentManageBinding>() {
    private val preferenceManager: PreferenceManager by lazy { PreferenceManager(requireActivity()) }
    lateinit var accessToken: String
    lateinit var refreshToken: String
    lateinit var memberName: String
    lateinit var memberUpdateInfoRequest: MemberUpdateInfoRequest

    override fun getViewBinding(): FragmentManageBinding =
        FragmentManageBinding.inflate(layoutInflater)

    override val viewModel: ManageViewModel
        get() = ManageViewModel(preferenceManager)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessToken = preferenceManager.getAccessToken()
        refreshToken = preferenceManager.getRefreshToken()

        initViews()

        mainScope {
            showProgress()

            viewModel.fetchMemberInfo(accessToken).let {
                when (it) {
                    is MemberState.Success<MemberInfoResponse> -> {
                        Log.d("fetch Success", "${it.data}")

                        initMemberInfo(it.data)
                    }
                    is MemberState.Unauthorized -> {
                        val dlg = SlothDialog(requireContext(), DialogState.FORBIDDEN)
                        dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                            override fun onItemClicked() {
                                preferenceManager.removeAuthToken()
                                startActivity(LoginActivity.newIntent(requireActivity()))
                            }
                        }
                        dlg.start()
                    }
                    is MemberState.Error -> {
                        Log.d("fetch error", "${it.exception}")
                    }
                    else -> Unit
                }
            }

            hideProgress()
        }
    }

    private fun initMemberInfo(data: MemberInfoResponse) {
        binding.apply {
            memberName = data.memberName
            tvManageProfileName.text = memberName
            tvManageProfileEmail.text = data.email
        }
    }

    override fun initViews() = with(binding) {

        ivManageProfileImage.setOnClickListener {
            // nickname change
            val updateDialog = Dialog(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
            updateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // dialog radius 적용
            updateDialog.setContentView(R.layout.dialog_manage_update_member_info)

            val nameEditText =
                updateDialog.findViewById<EditText>(R.id.et_manage_dialog_profile_name)
            val updateButton =
                updateDialog.findViewById<AppCompatButton>(R.id.btn_manage_dialog_update_member_info)

            nameEditText.hint = memberName

            focusInputForm(nameEditText, updateButton)

            updateButton.setOnClickListener {
                memberUpdateInfoRequest = MemberUpdateInfoRequest(
                    memberName = nameEditText.text.toString()
                )
                if (nameEditText.text.toString() != memberName) {
                    mainScope {
                        viewModel.updateMemberInfo(accessToken, memberUpdateInfoRequest).let {
                            when (it) {
                                is MemberState.Success<MemberUpdateInfoResponse> -> {
                                    Log.d("Update Success", "${it.data}")

                                    updateViews(it.data.memberName)

                                    Toast.makeText(
                                        requireContext(),
                                        "닉네임이 변경되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is MemberState.Unauthorized -> {
                                    Log.d("Update Error", "${it.exception}")
                                }

                                is MemberState.Error -> {
                                    Log.d("Update Error", "${it.exception}")
                                    Toast.makeText(
                                        requireContext(),
                                        "닉네임이 변경 실패하였습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "현재 닉네임과 동일합니다.", Toast.LENGTH_SHORT).show()
                }
                updateDialog.dismiss()
            }
            updateDialog.show()
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
                    preferenceManager.removeAuthToken()
                    startActivity(LoginActivity.newIntent(requireActivity()))
                }
            }
            dlg.start()
        }

        clManageWithdraw.setOnClickListener {
            val dlg = SlothDialog(requireContext(), DialogState.WITHDRAW)
            dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                override fun onItemClicked() {
                    preferenceManager.removeAuthToken()
                    Toast.makeText(requireContext(), "회원탈퇴 되었습니다", Toast.LENGTH_SHORT).show()
                    startActivity(LoginActivity.newIntent(requireActivity()))
                }
            }
            dlg.start()
        }
    }

    private fun updateViews(updateMemberName: String) {
        binding.tvManageProfileName.text = updateMemberName
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.sloth_official_mail)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_subject))
            putExtra(
                Intent.EXTRA_TEXT,
                String.format(
                    "---------------------------------------------\n나나공\nApp Version : %s\nAndroid(SDK) : %d(%s)\n Device Model : %s\n---------------------------------------------\n",
                    BuildConfig.VERSION_NAME,
                    Build.VERSION.SDK_INT,
                    Build.VERSION.RELEASE,
                    Build.MODEL
                )
            )
            type = "message/rfc822"
        }
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun unlockButton(button: AppCompatButton) {
        button.isEnabled = true
        button.background = getDrawable(requireContext(), R.drawable.bg_login_policy_rounded_sloth)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun lockButton(button: AppCompatButton) {
        button.isEnabled = false
        button.background = getDrawable(requireContext(), R.drawable.bg_login_policy_rounded_gray)
    }

    private fun focusInputForm(editText: EditText, button: AppCompatButton) {
        editText.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun beforeTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(editable: Editable?) {
                if (editable.isNullOrEmpty()) {
                    lockButton(button)
                } else {
                    unlockButton(button)
                }
            }
        })
    }
}