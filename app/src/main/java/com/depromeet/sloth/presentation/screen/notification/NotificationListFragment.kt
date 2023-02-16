package com.depromeet.sloth.presentation.screen.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.notification.NotificationListResponse
import com.depromeet.sloth.databinding.FragmentNotificationListBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.NotificationAdapter
import com.depromeet.sloth.presentation.screen.base.BaseFragment
import com.depromeet.sloth.presentation.screen.manage.UpdateMemberDialogFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class NotificationListFragment :
    BaseFragment<FragmentNotificationListBinding>(R.layout.fragment_notification_list) {

    private lateinit var notificationAdapter: NotificationAdapter
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = notificationViewModel
        }

        initListener()
        initObserver()

        notificationViewModel.fetchNotificationList()
    }

    private fun initListener() {
        notificationAdapter = NotificationAdapter {
            notificationViewModel.updateNotificationState(it) {
                Log.e("updateNotificationState", it.toString())
            }
        }
        binding.rvNotificationList.adapter = notificationAdapter

        binding.tbNotification.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() = with(notificationViewModel) {
        repeatOnStarted {

            launch {
                fetchLessonListSuccessEvent
                    .collect {
                        notificationAdapter.setNotificationList(it)
                    }
            }

            launch {
                isLoading
                    .collect { isLoading ->
                        when (isLoading) {
                            true -> showProgress()
                            false -> hideProgress()
                        }
                    }
            }

            launch {
                showToastEvent
                    .collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun navigateUp() {
        val action = UpdateMemberDialogFragmentDirections.actionUpdateMemberDialogToManage()
        findNavController().safeNavigate(action)
    }
}