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

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()

    override fun getActivityBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(layoutInflater)

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
                        Log.d("fetch Success", notificationRegisterState.data)
                    }

                    is NotificationRegisterState.Unauthorized -> {
                        showLogoutDialog(this@HomeActivity,
                            this@HomeActivity) { viewModel.removeAuthToken() }
                    }

                    is NotificationRegisterState.NotFound, NotificationRegisterState.Forbidden -> {
                        Toast.makeText(this@HomeActivity,
                            "강의 상세 정보를 가져오지 못했어요",
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                    is NotificationRegisterState.Error -> {
                        Log.d("Error", "${notificationRegisterState.exception}")
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
                    Log.d("FCM Token", fcmToken)
                }
            }
        }
    }
}