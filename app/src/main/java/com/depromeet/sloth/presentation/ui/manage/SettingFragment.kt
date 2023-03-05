package com.depromeet.sloth.presentation.ui.manage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentSettingBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.ui.base.BaseFragment
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


// TODO 화면 전환 시 푸시알림 수신버튼이 비활성화 -> 활성화되는 애니메이션이 보이는 현상 제거
@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: ManageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initListener()
        initObserver()
    }

    private fun initListener() {
        binding.tbSetting.setOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.navigateToContactEvent.collect {
                        sendEmail()
                    }
            }

            launch {
                viewModel.navigateToPrivatePolicyEvent.collect {
                        showPrivatePolicy()
                    }
            }

            launch {
                viewModel.navigateToLogoutDialogEvent.collect {
                        showLogoutDialog()
                    }
            }
        }
    }

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

    private fun showPrivatePolicy() {
        val action = SettingFragmentDirections.actionManageToSlothPolicyWebview()
        findNavController().safeNavigate(action)
    }

    private fun showLogoutDialog() {
        val action = SettingFragmentDirections.actionManageToLogoutDialog()
        findNavController().safeNavigate(action)
    }
}