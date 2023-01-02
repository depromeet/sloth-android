package com.depromeet.sloth.ui.manage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.member.MemberResponse
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.logout
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showToast
import com.depromeet.sloth.extensions.showWithdrawalDialog
import com.depromeet.sloth.ui.base.BaseFragment
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.util.CELLPHONE_INFO_DIVER
import com.depromeet.sloth.util.MESSAGE_TYPE
import com.depromeet.sloth.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO 화면 전환 시 푸시알림 수신버튼이 이동되는 애니메이션이 보이는 현상 제거
@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val manageViewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_home)

    override fun onStart() {
        super.onStart()
        manageViewModel.fetchMemberInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = manageViewModel
        }
        initObserver()
    }

    private fun initObserver() = with(manageViewModel) {
        repeatOnStarted {
            launch {
                fetchMemberInfoEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<MemberResponse> -> {
                                binding.manageNetworkError.itemNetworkError.visibility = View.GONE
                                setMemberInfo(result.data)
                            }

                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(
                                        requireContext(),
                                        this@ManageFragment
                                    ) { removeAuthToken() }

                                    else -> {
                                        Timber.tag("Fetch Error").d(result.throwable)
                                        showToast(
                                            requireContext(),
                                            getString(R.string.member_info_fetch_fail)
                                        )
                                        binding.manageNetworkError.itemNetworkError.visibility =
                                            View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                updateNotificationReceiveEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> {
                                showToast(
                                    requireContext(),
                                    getString(R.string.noti_update_complete)
                                )
                                setMemberNotificationReceive(binding.scManageNotificationStatus.isChecked)
                            }

                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(
                                        requireContext(),
                                        this@ManageFragment
                                    ) {
                                        removeAuthToken()
                                    }

                                    else -> {
                                        Timber.tag("Update Error").d(result.throwable)
                                        showToast(
                                            requireContext(),
                                            getString(R.string.noti_update_fail)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                logoutEvent
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress()
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> logout(
                                requireContext(), this@ManageFragment
                            ) { removeAuthToken() }

                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(
                                        requireContext(),
                                        this@ManageFragment
                                    ) { removeAuthToken() }

                                    else -> {
                                        Timber.tag("Logout Error").d(result.throwable)
                                        showToast(requireContext(), getString(R.string.logout_fail))
                                    }
                                }
                            }
                        }
                    }
            }

            launch {
                navigateToUpdateProfileDialogEvent
                    .collect { showProfileUpdateDialog() }
            }

            launch {
                navigateToContactEvent
                    .collect { sendEmail() }
            }

            launch {
                navigateToPrivatePolicyEvent
                    .collect { showPrivatePolicy() }
            }

            launch {
                navigateToLogoutDialogEvent
                    .collect { showLogoutDialog() }
            }

            launch {
                navigateToWithdrawalDialogEvent
                    .collect {
                        showWithdrawalDialog(
                            requireContext(),
                            this@ManageFragment
                        ) { manageViewModel.removeAuthToken() }
                    }
            }
        }
    }

    private fun showProfileUpdateDialog() {
        val action = ManageFragmentDirections.actionManageToUpdateMemberFragment()
        findNavController().safeNavigate(action)
    }

    private fun showPrivatePolicy() {
        val action = ManageFragmentDirections.actionManageToSlothPolicyWebview()
        findNavController().safeNavigate(action)
    }

    private fun showLogoutDialog() {
        val dlg = SlothDialog(requireContext(), DialogState.LOGOUT)
        dlg.onItemClickListener =
            object : SlothDialog.OnItemClickedListener {
                override fun onItemClicked() {
                    manageViewModel.logout()
                }
            }
        dlg.show()
    }

    private fun sendEmail() {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(getString(R.string.sloth_official_mail))
                )
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }
}