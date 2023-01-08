package com.depromeet.sloth.ui

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityMainBinding
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun preload() {
        installSplashScreen()
    }

    override fun init() {
        // initViews()
        initNavigation()
    }

    private fun initViews() {
        // Crashlytics 비정상 종료 테스트
        val crashButton = Button(this)
        crashButton.text = getString(R.string.test_crash)
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(
            crashButton, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun initNavigation() = with(binding) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_home_container) as NavHostFragment

        val navController = navHostFragment.findNavController()

        bnvHome.apply {
            setupWithNavController(navController)
            itemIconTintList = null
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.today_lesson, R.id.lesson_list, R.id.manage -> bnvHome.visibility =
                    View.VISIBLE

                else -> bnvHome.visibility = View.GONE
            }
        }
    }
}