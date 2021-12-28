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
import com.depromeet.sloth.data.network.member.UpdateMemberInfoRequest
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.login.SlothPolicyWebViewActivity

class ManageFragment : BaseFragment<ManageViewModel, FragmentManageBinding>() {
    private val pm: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: ManageViewModel = ManageViewModel()

    override fun getViewBinding(): FragmentManageBinding =
        FragmentManageBinding.inflate(layoutInflater)

    lateinit var accessToken: String

    lateinit var refreshToken: String

    lateinit var memberName: String

    lateinit var updateMemberInfoRequest: UpdateMemberInfoRequest

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessToken = pm.getAccessToken().toString()
        refreshToken = pm.getRefreshToken().toString()

        initViews()

        mainScope {
            binding.pbManage.visibility = View.VISIBLE

            viewModel.fetchMemberInfo(accessToken).let {
                when (it) {
                    is MemberState.Success -> {
                        Log.d("fetch Success", "${it.data}")

                        initMemberInfo(it.data)
                    }

                    is MemberState.Unauthorized -> {
                        viewModel.fetchMemberInfo(accessToken = refreshToken)
                            .let { memberResponse ->
                                when (memberResponse) {
                                    is MemberState.Success -> {
                                        Log.d("fetch Success", "${memberResponse.data}")

                                        initMemberInfo(memberResponse.data)
                                    }

                                    is MemberState.Error -> {
                                        Log.d("fetch Error", "${memberResponse.exception}")
                                    }
                                    else -> Unit
                                }
                            }
                    }


                    is MemberState.Error -> {
                        Log.d("fetch error", "${it.exception}")
                    }
                    else -> Unit
                }
            }
            binding.pbManage.visibility = View.GONE
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
                updateMemberInfoRequest = UpdateMemberInfoRequest(
                    memberName = nameEditText.text.toString()
                )
                if (nameEditText.text.toString() != memberName) {
                    mainScope {
                        viewModel.updateMemberInfo(accessToken, updateMemberInfoRequest).let {
                            when (it) {
                                is MemberState.Success -> {
                                    Log.d("Update Success", "${it.data}")

                                    updateViews(it.data.memberName)

                                    Toast.makeText(requireContext(),
                                        "닉네임이 변경되었습니다.",
                                        Toast.LENGTH_SHORT).show()
                                }

                                is MemberState.Unauthorized -> {
                                    viewModel.updateMemberInfo(accessToken = refreshToken,
                                        updateMemberInfoRequest).let { memberState ->
                                        when (memberState) {
                                            is MemberState.Success -> {
                                                Log.d("Update Success", "${memberState.data}")

                                                updateViews(memberState.data.memberName)

                                                Toast.makeText(requireContext(),
                                                    "닉네임이 변경되었습니다.",
                                                    Toast.LENGTH_SHORT).show()
                                            }

                                            is MemberState.Error -> {
                                                Log.d("Update Error", "${memberState.exception}")
                                                Toast.makeText(requireContext(),
                                                    "닉네임이 변경 실패하였습니다.",
                                                    Toast.LENGTH_SHORT).show()
                                            }
                                            else -> Unit
                                        }
                                    }
                                }

                                is MemberState.Error -> {
                                    Log.d("Update Error", "${it.exception}")
                                    Toast.makeText(requireContext(),
                                        "닉네임이 변경 실패하였습니다.",
                                        Toast.LENGTH_SHORT).show()
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
                    mainScope {
                        // logout
                        viewModel.removeAuthToken(pm)
                        // finish
                        startActivity(LoginActivity.newIntent(requireActivity()))
                    }
                }
            }
            dlg.start()
        }

        clManageWithdraw.setOnClickListener {
            val dlg = SlothDialog(requireContext(), DialogState.WITHDRAW)
            dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                override fun onItemClicked() {
                    //withdraw

                    //finish
                    mainScope {
                        viewModel.removeAuthToken(pm)
                        startActivity(LoginActivity.newIntent(requireActivity()))
                    }
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
            putExtra(Intent.EXTRA_TEXT,
                String.format("---------------------------------------------\n나나공\nApp Version : %s\nAndroid(SDK) : %d(%s)\n Device Model : %s\n---------------------------------------------\n",
                    BuildConfig.VERSION_NAME,
                    Build.VERSION.SDK_INT,
                    Build.VERSION.RELEASE,
                    Build.MODEL))
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