package com.depromeet.sloth.presentation.screen.manage

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
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * @author 최철훈
 * @created 2023-02-16
 * @desc
 */
@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val manageViewModel: ManageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = manageViewModel
        }

        init()
        initObserver()
    }

    private fun init() {
        binding.tbSetting.setOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() = with(manageViewModel) {
        repeatOnStarted {
            launch {
                navigateToContactEvent
                    .collect {
                        sendEmail()
                    }
            }

            launch {
                navigateToPrivatePolicyEvent
                    .collect {
                        showPrivatePolicy()
                    }
            }

            launch {
                navigateToLogoutDialogEvent
                    .collect {
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