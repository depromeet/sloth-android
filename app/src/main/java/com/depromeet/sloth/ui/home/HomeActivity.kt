package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.model.response.notification.NotificationFetchResponse
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.util.LoadingDialogUtil.hideProgress
import com.depromeet.sloth.util.LoadingDialogUtil.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var navController: NavController

    //TODO Datasource 로 옮기기
    private val deviceId: String by lazy {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 최초 실행시에만 호출 - 이렇게 간단하게 해결해줄 수도 있음
        // preference 에 fcmToken 을 저장을 해서 있나 없나만 확인하는 방법을 사용하면 앱을 실행할때마다 fetch Api를 호출할 필요가 없게됨
        // 로그아웃할때 preference 에 fcm 토큰 제거해주면 다중 로그인문제도 해결
        if (savedInstanceState == null) {
            homeViewModel.fetchFCMToken(deviceId)
        }

        initNavigation()
        initObserver()
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
                            is Result.UnLoading -> hideProgress()
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
                        }
                    }
            }

            launch {
                notificationRegisterState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress(this@HomeActivity)
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> Timber.d(result.data)
                            is Result.Unauthorized -> showForbiddenDialog(this@HomeActivity) { homeViewModel.removeAuthToken() }
                            is Result.Error -> Timber.tag("Error").d(result.throwable)
                        }
                    }
            }
        }
    }
}