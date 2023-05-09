package com.depromeet.presentation.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.adapter.NotificationAdapter
import com.depromeet.presentation.databinding.FragmentNotificationListBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class NotificationListFragment :
    BaseFragment<FragmentNotificationListBinding>(R.layout.fragment_notification_list) {

    private val viewModel: NotificationViewModel by viewModels()

    // TODO 화면 이동 이벤트 추가
    private val notificationItemClickListener = NotificationItemClickListener(
        onRestartOnBoardingClick = { viewModel.navigateToOnBoardingTodayLesson() },
        onNotificationClick = { notification ->
            viewModel.updateNotificationReadStatus(notification) {
                Timber.tag("updateNotificationState").d(notification.toString())
            }
        }
    )

    private val notificationAdapter by lazy {
        NotificationAdapter(notificationItemClickListener)
        // NotificationPagingAdapter(notificationItemClickListener)
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
        /*
        binding.rvNotificationList.adapter = notificationAdapter.withLoadStateFooter(
            footer = NotificationLoadStateAdapter(
                notificationAdapter::retry
            )
        )
         */
    }

    private fun initListener() {
        binding.tbNotification.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().finish()
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.fetchLessonListSuccessEvent.collect {
                    notificationAdapter.submitList(it)
                }
                /*
                viewModel.notificationList.collectLatest {
                    notificationAdapter.submitData(it)
                }
                 */
            }

            launch {
                viewModel.navigateToOnBoardingTodayLessonEvent.collect {
                    navigateToOnBoardingTodayLesson()
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

    private fun navigateToOnBoardingTodayLesson() {
        val action = NotificationListFragmentDirections.actionNotificationListToOnBoardingTodayLesson()
        findNavController().safeNavigate(action)
    }
}