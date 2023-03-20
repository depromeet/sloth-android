package com.depromeet.presentation.ui.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentManageBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val viewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_main)

    override fun onStart() {
        super.onStart()
        viewModel.fetchLessonStatisticsInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initObserver()
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.navigateToUpdateMemberDialogEvent.collect {
                        showUpdateMemberDialog()
                    }
            }

            launch {
                viewModel.navigateToSettingEvent.collect {
                    val action = ManageFragmentDirections.actionManageToSetting()
                    findNavController().safeNavigate(action)
                }
            }

            launch {
                viewModel.navigateToWithdrawalDialogEvent.collect {
                        showWithdrawalDialog()
                    }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.navigateToExpireDialogEvent.collect {
                        showExpireDialog()
                    }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun showUpdateMemberDialog() {
        val action = ManageFragmentDirections.actionManageToUpdateMemberDialog(
            viewModel.uiState.value.memberName
        )
        findNavController().safeNavigate(action)
    }

    // 회원 탈퇴 API 필요
    private fun showWithdrawalDialog() = Unit

}