package com.depromeet.sloth.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.notification.NotificationRegisterRequest
import com.depromeet.sloth.data.network.notification.NotificationRegisterState
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.util.LoadingDialogUtil
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavigation()

        viewModel.apply {
            val fcmToken =  viewModel.getFCMToken()
            Timber.d("fcmToken: $fcmToken")
            if(fcmToken.isEmpty()) {
                registerFCMToken()
            }

            notificationRegisterState.observe(this@HomeActivity) { notificationRegisterState ->
                when (notificationRegisterState) {
                    is NotificationRegisterState.Loading -> {
                        handleLoadingState(this@HomeActivity)
                    }

                    is NotificationRegisterState.Success<String> -> {
                        Timber.tag("fetch Success").d(notificationRegisterState.data)
                    }

                    is NotificationRegisterState.Unauthorized -> {
                        showLogoutDialog(this@HomeActivity) { viewModel.removeAuthToken() }
                    }

                    is NotificationRegisterState.NotFound, NotificationRegisterState.Forbidden -> {
                        showToast("fcm 토큰을 저장하지 못했어요")
                    }

                    is NotificationRegisterState.Error -> {
                        Timber.tag("Error").d(notificationRegisterState.exception)
                    }

                    else -> Unit
                }
                LoadingDialogUtil.hideProgress()
            }
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


    private fun registerFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w(task.exception, "Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            Timber.tag("FCM Token").d(fcmToken)
            viewModel.putFCMToken(fcmToken)
            mainScope {
                viewModel.registerFCMToken(NotificationRegisterRequest(fcmToken))
            }
        }
    }
}