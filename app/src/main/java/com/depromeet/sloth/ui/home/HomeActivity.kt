package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.common.Result
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.ui.base.BaseActivity
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

        // 앱 진입시 fcm 토큰을 서버에 전달하는 방식으로 로직 변경
        if (savedInstanceState == null) {
            homeViewModel.createAndRegisterFCMToken(deviceId)
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
                notificationRegisterState
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> showProgress(this@HomeActivity)
                            is Result.UnLoading -> hideProgress()
                            is Result.Success<String> -> Timber.d(result.data)
                            is Result.Error -> {
                                when (result.statusCode) {
                                    401 -> showForbiddenDialog(this@HomeActivity) {
                                        homeViewModel.removeAuthToken()
                                    }
                                    else -> Timber.tag("Register Error").d(result.throwable)
                                }
                            }
                        }
                    }
            }
        }
    }
}