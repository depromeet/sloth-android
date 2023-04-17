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
import com.depromeet.presentation.util.setOnMenuItemSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ManageFragment : BaseFragment<FragmentManageBinding>(R.layout.fragment_manage) {

    private val viewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_main)

    override fun onStart() {
        super.onStart()
        viewModel.fetchUserProfile()
        viewModel.fetchLessonStatisticsInfo()
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
        binding.tbManage.setOnMenuItemSingleClickListener {
            when (it.itemId) {
                R.id.menu_setting -> {
                    viewModel.navigateToSetting()
                    true
                }
                else -> false
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.navigateToUpdateUserProfileDialogEvent.collect {
                    showUpdateUserProfileDialog()
                }
            }

            launch {
                viewModel.navigateToSettingEvent.collect {
                    val action = ManageFragmentDirections.actionManageToSetting()
                    findNavController().safeNavigate(action)
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

    private fun showUpdateUserProfileDialog() {
        val action = ManageFragmentDirections.actionManageToUpdateUserProfileDialog(
            viewModel.uiState.value.userName
        )
        findNavController().safeNavigate(action)
    }
}