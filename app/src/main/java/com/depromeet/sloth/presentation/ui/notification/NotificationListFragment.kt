package com.depromeet.sloth.presentation.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentNotificationListBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.presentation.adapter.NotificationAdapter
import com.depromeet.sloth.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
internal class NotificationListFragment :
    BaseFragment<FragmentNotificationListBinding>(R.layout.fragment_notification_list) {

    private val viewModel: NotificationViewModel by viewModels()

    // TODO 화면 이동 이벤트 추가
    private val notificationItemClickListener = NotificationItemClickListener {
        viewModel.updateNotificationState(it) {
            Timber.tag("updateNotificationState").d(it.toString())
        }
    }

    private val notificationAdapter by lazy {
        NotificationAdapter(notificationItemClickListener)
    }

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
        binding.rvNotificationList.adapter = notificationAdapter
    }

    private fun initListener() {
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
                        notificationAdapter.submitList(it)
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
}