package com.depromeet.presentation.ui.manage

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony.BaseMmsColumns.MESSAGE_TYPE
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentSettingBinding
import com.depromeet.presentation.extensions.getPackageInfoCompat
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseFragment
import com.depromeet.presentation.util.CELLPHONE_INFO_DIVER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


// TODO 처음 세팅 화면에 진입했을때 스위치 버튼의 애니메이션이 보이는 현상 해결(비활성화 -> 활성화)
@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    lateinit var versionName: String
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
        val context: Context = requireActivity().applicationContext
        val packageManager: PackageManager = context.packageManager
        val packageName: String = context.packageName

        try {
            versionName = packageManager.getPackageInfoCompat(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        startActivity(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.sloth_official_mail)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_subject))
            putExtra(
                Intent.EXTRA_TEXT,
                String.format(
                    CELLPHONE_INFO_DIVER,
                    versionName,
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