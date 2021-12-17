package com.depromeet.sloth.ui.home.mypage

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
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.mypage.MypageResponse
import com.depromeet.sloth.data.network.mypage.MypageState
import com.depromeet.sloth.databinding.FragmentMypageBinding
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.login.LoginActivity

class MypageFragment: BaseFragment<MypageViewModel, FragmentMypageBinding>() {
    private val pm: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: MypageViewModel = MypageViewModel()

    override fun getViewBinding(): FragmentMypageBinding = FragmentMypageBinding.inflate(layoutInflater)

    lateinit var accessToken: String

    lateinit var refreshToken: String

    lateinit var memberName: String

    lateinit var updateMemberName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*액티비티의 변수 사용*/
        //accessToken = (activity as? HomeActivity)?.accessToken ?: ""

        accessToken = pm.getAccessToken().toString()
        refreshToken = pm.getRefreshToken().toString()

        initViews()

        mainScope {
            binding.pbMypage.visibility = View.VISIBLE

            viewModel.fetchMemberInfo(accessToken).let {
                when(it) {
                    is MypageState.Success -> {
                        Log.d( "fetch Success", "${it.data}")

                        initMemberInfo(it.data)
                    }

                    is MypageState.Unauthorized -> {
                        viewModel.fetchMemberInfo(accessToken = refreshToken).let { mypageResponse ->
                            when (mypageResponse) {
                                is MypageState.Success -> {
                                    Log.d("fetch Success", "${mypageResponse.data}")

                                    initMemberInfo(mypageResponse.data)
                                }

                                is MypageState.Error -> {
                                    Log.d("fetch Error", "${mypageResponse.exception}")
                                }
                                else -> Unit
                            }
                        }
                    }


                    is MypageState.Error -> {
                        Log.d("fetch error", "${it.exception}")
                    }
                    else -> Unit
                }
            }
            binding.pbMypage.visibility = View.GONE
        }
    }

    private fun initMemberInfo(data: MypageResponse) {
        binding.apply {
            memberName = data.memberName
            tvMypageProfileName.text = memberName
            tvMypageProfileEmail.text = data.email
        }
    }

    override fun initViews() = with(binding) {

        ivMypageProfileImage.setOnClickListener {
            // nickname change
            val updateDialog = Dialog(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
            updateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // dialog radius 적용
            updateDialog.setContentView(R.layout.dialog_mypage_update_member_info)

            val nameEditText = updateDialog.findViewById<EditText>(R.id.et_mypage_dialog_profile_name)
            val updateButton = updateDialog.findViewById<AppCompatButton>(R.id.btn_mypage_dialog_update_member_info)

            nameEditText.hint = memberName

            focusInputForm(nameEditText, updateButton)

            updateButton.setOnClickListener {
                updateMemberName = nameEditText.text.toString()
                if(updateMemberName != memberName) {
                    mainScope {
                        viewModel.updateMemberInfo(accessToken, updateMemberName).let {
                            when (it) {
                                is MypageState.Success -> {
                                    Log.d("Update Success", "${it.data}")
                                    updateViews(updateMemberName)

                                    Toast.makeText(requireContext(), "닉네임이 변경되었습니다.",Toast.LENGTH_SHORT).show()
                                }

                                is MypageState.Unauthorized -> {
                                    viewModel.updateMemberInfo(accessToken = refreshToken, updateMemberName).let { mypageState ->
                                        when (mypageState) {
                                            is MypageState.Success -> {
                                                Log.d("Update Success", "${mypageState.data}")
                                                updateViews(updateMemberName)

                                                Toast.makeText(requireContext(), "닉네임이 변경되었습니다.",Toast.LENGTH_SHORT).show()
                                            }

                                            is MypageState.Error -> {
                                                Log.d("Update Error", "${mypageState.exception}")
                                                Toast.makeText(requireContext(), "닉네임이 변경 실패하였습니다.",Toast.LENGTH_SHORT).show()
                                            }
                                            else -> Unit
                                        }
                                    }
                                }

                                is MypageState.Error -> {
                                    Log.d("Update Error", "${it.exception}")
                                    Toast.makeText(requireContext(), "닉네임이 변경 실패하였습니다.",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(requireContext(), "현재 닉네임과 동일합니다.",Toast.LENGTH_SHORT).show()
                }
                updateDialog.dismiss()
            }
            updateDialog.show()
        }

        clMypageContact.setOnClickListener {
            sendEmail()
        }

        clMypageLogout.setOnClickListener {
            val dlg = LogoutDialog(requireContext())
            dlg.listener = object: LogoutDialog.LogoutDialogClickedListener {
                override fun onLogoutClicked() {
                    //logout

                    //finish
                    mainScope {
                        viewModel.removeAuthToken(pm)
                        startActivity(LoginActivity.newIntent(requireActivity()))
                    }
                    Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
            dlg.start()
        }

        clMypageWithdraw.setOnClickListener {
            val dlg = WithdrawDialog(requireContext())
            dlg.listener = object: WithdrawDialog.WithdrawDialogClickedListener {
                override fun onWithdrawClicked() {
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
        binding.tvMypageProfileName.text = updateMemberName
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.sloth_official_mail)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_subject))
            putExtra(Intent.EXTRA_TEXT,
                String.format("---------------------------------------------\n나나공\nApp Version : %s\nAndroid(SDK) : %d(%s)\n Device Model : %s\n---------------------------------------------\n",
                BuildConfig.VERSION_NAME, Build.VERSION.SDK_INT, Build.VERSION.RELEASE, Build.MODEL))
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