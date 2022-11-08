package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.notification.fetch.NotificationFetchResponse
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.common.Result
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var navController: NavController

    private val deviceId: String by lazy {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavigation()
        initObserver()
    }

    override fun onStart() {
        super.onStart()
        // 서버에 저장된 FCM 토큰이 있는지 조회 후
        // 존재하지 않는다면 토큰을 생성하여 서버에 저장
        // 이미 존재한다면 토큰을 재 생성하지 않음
        homeViewModel.fetchFCMToken(deviceId)
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

    private fun initObserver() = with(homeViewModel) {
        repeatOnStarted {
            launch {
                notificationFetchState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress(this@HomeActivity)
                            is Result.Success<NotificationFetchResponse> -> {
                                if (result.data.fcmToken == null) {
                                    Timber.d("fcmToken not existed")
                                    createAndRegisterFCMToken(deviceId)
                                } else {
                                    Timber.d("fcmToken already existed")
                                }
                            }
                            is Result.Unauthorized -> showForbiddenDialog(this@HomeActivity) { homeViewModel.removeAuthToken() }
                            is Result.Error -> Timber.tag("Error").d(result.throwable)
                            else -> {}
                        }
                        hideProgress()
                    }
            }

            launch {
                notificationRegisterState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress(this@HomeActivity)
                            is Result.Success<String> -> Timber.d(result.data)
                            is Result.Unauthorized -> showForbiddenDialog(this@HomeActivity) { homeViewModel.removeAuthToken() }
                            is Result.Error -> Timber.tag("Error").d(result.throwable)
                            else -> {}
                        }
                        hideProgress()
                    }
            }
        }
    }
}