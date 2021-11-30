package com.depromeet.sloth.ui.home.mypage

import android.app.Dialog
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
import com.depromeet.sloth.R
import com.depromeet.sloth.data.db.PreferenceManager
import com.depromeet.sloth.data.network.mypage.MypageState
import com.depromeet.sloth.databinding.FragmentMypageBinding
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.home.HomeActivity

class MypageFragment: BaseFragment<MypageViewModel, FragmentMypageBinding>() {
//    private val preferenceManager: PreferenceManager by lazy { PreferenceManager(requireActivity()) }

    override val viewModel: MypageViewModel = MypageViewModel()

    override fun getViewBinding(): FragmentMypageBinding = FragmentMypageBinding.inflate(layoutInflater)

    lateinit var accessToken: String

    lateinit var memberName: String

    lateinit var updateMemberName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*액티비티의 변수 사용*/
        accessToken = (activity as? HomeActivity)?.accessToken ?: ""
        //accessToken = preferenceManager.getAccessToken().toString()

        initViews()

        mainScope {

            binding.pbMypage.visibility = View.VISIBLE

            viewModel.fetchMemberInfo(accessToken).let {
                when(it) {
                    is MypageState.Success -> {
                        Log.d( "fetch Success", "${it.data}")

                        binding.apply {
                            memberName = it.data.memberName
                            tvMypageProfileName.text = memberName
                            tvMypageProfileEmail.text = it.data.email
                        }
                    }

                    is MypageState.Error -> {
                        Log.d("fetch error", "${it.exception}")
                    }
                }
            }
            binding.pbMypage.visibility = View.GONE
        }
    }

    override fun initViews() = with(binding) {

        ivMypageProfileImage.setOnClickListener {
            //profile, nickname change
            val updateDialog = Dialog(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
            updateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            /*dialog radius 적용*/
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
                                }

                                is MypageState.Error -> {
                                    Log.d("Update Error", "${it.exception}")
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