package com.depromeet.sloth.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.notification.NotificationSaveRequest
import com.depromeet.sloth.data.network.notification.NotificationSaveState
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.ui.base.BaseActivity
import com.depromeet.sloth.ui.list.ListFragment
import com.depromeet.sloth.ui.manage.ManageFragment
import com.depromeet.sloth.ui.list.TodayFragment
import com.depromeet.sloth.ui.login.LoginActivity
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val viewModel: HomeViewModel by viewModels()

    override fun getViewBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(layoutInflater)

    lateinit var accessToken: String
    lateinit var refreshToken: String
    lateinit var fcmToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accessToken = preferenceManager.getAccessToken()
        refreshToken = preferenceManager.getRefreshToken()

        supportFragmentManager.fragmentFactory = SlothFragmentFactory()

        initNavigationEvent()

        if(::fcmToken.isInitialized.not()){
            registerFCMToken()
        }
    }

    private fun initNavigationEvent() {
        binding.navigationView.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_today -> changeFragment(TodayFragment::class.java.name)
                    R.id.menu_class -> changeFragment(ListFragment::class.java.name)
                    R.id.menu_mypage -> changeFragment(ManageFragment::class.java.name)
                }

                true
            }
            this.selectedItemId = R.id.menu_today
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
                        accessToken,
                        NotificationSaveRequest(fcmToken)
                    ).let {
                        when(it) {
                            is NotificationSaveState.Success<String> -> {
                                Log.d("register Success", it.data)
                            }
                            is NotificationSaveState.Error -> {
                                Log.d("register Error", "${it.exception}")
                            }
                            is NotificationSaveState.Unauthorized -> {
                                val dlg = SlothDialog(this, DialogState.FORBIDDEN)
                                dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
                                    override fun onItemClicked() {
                                        preferenceManager.removeAuthToken()
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

                preferenceManager.putFCMToken(fcmToken)
                Log.d("FCM Token", fcmToken)
            }
        }
    }
}