package com.depromeet.sloth.presentation.ui.manage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
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


// TODO 처음 세팅 화면에 진입했을때 스위치 버튼의 애니메이션이 보이는 현상 해결(비활성화 -> 활성화)
@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_main)

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