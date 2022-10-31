package com.depromeet.sloth.ui

import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.notification.NotificationState
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.data.network.notification.register.NotificationRegisterRequest
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var navController: NavController

    private val deviceId: String by lazy {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavigation()
        initObserver()

        viewModel.apply {
            // 서버에 저장된 FCM 토큰이 있는지 조회 후
            // 존재하지 않는다면 토큰을 생성하여 서버에 저장
            // 이미 존재한다면 토큰을 재 생성하지 않음
            fetchFCMToken(deviceId)
        }
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_home_container) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.bnvHome.apply {
            setupWithNavController(navController)
            itemIconTintList = null
        }
    }

    private fun initObserver() {
        viewModel.apply {
            notificationFetchState.observe(this@HomeActivity) { notificationState ->
                when (notificationState) {
                    is NotificationState.Loading -> {
                        showProgress(this@HomeActivity)
                    }

                    is NotificationState.Success<NotificationFetchResponse> -> {
                        Timber.tag("fetch Success").d("${notificationState.data}")
                        if (notificationState.data.fcmToken == null) {
                            Timber.d("fcmToken not existed")
                            registerFCMToken()
                        } else {
                            Timber.d("fcmToken already existed")
                        }
                    }

                    is NotificationState.Unauthorized -> {
                        showLogoutDialog(this@HomeActivity) { viewModel.removeAuthToken() }
                    }

                    is NotificationState.Error -> {
                        Timber.tag("Error").d(notificationState.exception)
                    }

                    else -> {}

                }
                hideProgress()
            }


            notificationRegisterState.observe(this@HomeActivity) { notificationState ->
                when (notificationState) {
                    is NotificationState.Loading -> {
                        showProgress(this@HomeActivity)
                    }

                    is NotificationState.Success<String> -> {
                        Timber.tag("fetch Success").d(notificationState.data)
                    }

                    is NotificationState.Unauthorized -> {
                        showLogoutDialog(this@HomeActivity) { viewModel.removeAuthToken() }
                    }

                    is NotificationState.Error -> {
                        Timber.tag("Error").d(notificationState.exception)
                    }

                    else -> {}
                }
                hideProgress()
            }
        }
    }


    private fun registerFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w(task.exception, "Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            Timber.tag("FCM Token is created").d(fcmToken)
            viewModel.registerFCMToken(NotificationRegisterRequest(deviceId, fcmToken))
        }
    }
}