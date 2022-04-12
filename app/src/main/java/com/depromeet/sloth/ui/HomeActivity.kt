package com.depromeet.sloth.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.notification.NotificationSaveRequest
import com.depromeet.sloth.data.network.notification.NotificationSaveState
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.list.ListFragment
import com.depromeet.sloth.ui.list.TodayFragment
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.ui.manage.ManageFragment
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

        // bottom navigation view icon 의 원래 색상 부여를 위해 (그라데이션)
        binding.bnvHome.itemIconTintList = null

//        supportFragmentManager.fragmentFactory = SlothFragmentFactory()
//        initNavigationEvent()

        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)?.findNavController()
        navController?.let {
            binding.bnvHome.setupWithNavController(it)
        }

        if (::fcmToken.isInitialized.not()) {
            registerFCMToken()
        }
    }

    private fun initNavigationEvent() = with(binding) {
        bnvHome.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_today -> changeFragment(TodayFragment::class.java.name)
                R.id.navigation_list -> changeFragment(ListFragment::class.java.name)
                R.id.navigation_manage -> changeFragment(ManageFragment::class.java.name)
            }
            true
        }
        bnvHome.selectedItemId = R.id.navigation_today

        bnvHome.setOnItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_today -> {}
                R.id.navigation_list -> {}
                R.id.navigation_manage -> {}
            }
        }
    }

    private fun changeFragment(className: String) {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, className)
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun registerFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result ?: ""
                mainScope {
                    viewModel.saveFCMToken(
                        NotificationSaveRequest(fcmToken)
                    ).let {
                        when (it) {
                            is NotificationSaveState.Success<String> -> {
                                Log.d("register Success", it.data)
                            }
                            is NotificationSaveState.Error -> {
                                Log.d("register Error", "${it.exception}")
                            }
                            is NotificationSaveState.Unauthorized -> {
                                val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                                dlg.onItemClickListener =
                                    object : SlothDialog.OnItemClickedListener {
                                        override fun onItemClicked() {
                                            viewModel.removeAuthToken()
                                            startActivity(LoginActivity.newIntent(this@HomeActivity))
                                        }
                                    }
                                dlg.start()
                            }
                            is NotificationSaveState.Created -> {
                                Log.d("Error", "Created")
                            }
                            is NotificationSaveState.NotFound -> {
                                Log.d("Error", "NotFound")
                            }
                            is NotificationSaveState.Forbidden -> {
                                Log.d("Error", "Forbidden")
                            }
                        }
                    }
                }

                viewModel.putFCMToken(fcmToken)
                Log.d("FCM Token", fcmToken)
            }
        }
    }
}