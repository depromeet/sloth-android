package com.depromeet.sloth.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.ui.list.ListFragment
import com.depromeet.sloth.ui.manage.ManageFragment
import com.depromeet.sloth.ui.list.TodayFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.fragmentFactory = SlothFragmentFactory()
        initNavigationEvent()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result ?: ""
//                FirebaseService.token = fcmToken
//                Log.d("FCM Token", "${FirebaseService.token}")
                preferenceManager.putFCMToken(fcmToken)
                Log.d("FCM Token", fcmToken)
            }
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
}