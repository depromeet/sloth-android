package com.depromeet.sloth.presentation.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentNotificationListBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.presentation.adapter.NotificationAdapter
import com.depromeet.sloth.presentation.ui.base.BaseFragment
import com.depromeet.sloth.presentation.ui.manage.UpdateMemberDialogFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
internal class NotificationListFragment :
    BaseFragment<FragmentNotificationListBinding>(R.layout.fragment_notification_list) {

    private lateinit var notificationAdapter: NotificationAdapter
    private val viewModel: NotificationViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.fetchNotificationList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initViews()
        initListener()
        initObserver()
    }

    override fun initViews() {
        super.initViews()
    }

    private fun initListener() {
        notificationAdapter = NotificationAdapter {
            viewModel.updateNotificationState(it) {
                Timber.tag("updateNotificationState").e(it.toString())
            }
        }
        binding.rvNotificationList.adapter = notificationAdapter

        binding.tbNotification.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver()  {
        repeatOnStarted {
            launch {
                viewModel.fetchLessonListSuccessEvent.collect {
                        notificationAdapter.setNotificationList(it)
                    }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.showToastEvent.collect { message ->
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