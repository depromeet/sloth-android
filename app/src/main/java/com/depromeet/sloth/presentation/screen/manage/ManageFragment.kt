package com.depromeet.sloth.presentation.screen.manage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentManageBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO 화면 전환 시 푸시알림 수신버튼이 비활성화 -> 활성화되는 애니메이션이 보이는 현상 제거
@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val viewModel: ManageViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.fetchMemberInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initListener()
        initObserver()
    }

    private fun initListener() {
        binding.tvSetting.setOnClickListener {
            navigateToSetting()
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.navigateToUpdateMemberDialogEvent.collect {
                        showUpdateMemberDialog()
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

    private fun navigateToSetting() {
        val action = ManageFragmentDirections.actionManageToSetting()
        findNavController().safeNavigate(action)
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