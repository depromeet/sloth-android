package com.depromeet.sloth.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
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

    lateinit var fcmToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (::fcmToken.isInitialized.not()) {
            registerFCMToken()
        }

        initNavigation()

        viewModel.apply {
            notificationRegisterState.observe(this@HomeActivity) { notificationRegisterState ->
                when (notificationRegisterState) {
                    is NotificationRegisterState.Loading -> {
                        handleLoadingState(this@HomeActivity)
                    }

                    is NotificationRegisterState.Success<String> -> {
                        Timber.tag("fetch Success").d(notificationRegisterState.data)
                    }

                    is NotificationRegisterState.Unauthorized -> {
                        showLogoutDialog(this@HomeActivity,
                            this@HomeActivity) { viewModel.removeAuthToken() }
                    }

                    is NotificationRegisterState.NotFound, NotificationRegisterState.Forbidden -> {
                        showToast("강의 상세 정보를 가져오지 못했어요")
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
        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_home_container)
            ?.findNavController()
        navController?.let {
            binding.bnvHome.setupWithNavController(it)
        }
        binding.bnvHome.itemIconTintList = null
    }

    private fun registerFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result ?: ""
                mainScope {
                    viewModel.registerFCMToken(NotificationRegisterRequest(fcmToken))
                    viewModel.putFCMToken(fcmToken)
                    Timber.tag("FCM Token").d(fcmToken)
                }
            }
        }
    }
}