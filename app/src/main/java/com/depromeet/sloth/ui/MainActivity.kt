package com.depromeet.sloth.ui

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
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

            //system back button 을 누를 경우 startDestination 만 back stack 에 남도록 설정
            setOnItemSelectedListener { menuItem ->
                val builder = NavOptions.Builder()
                    .setPopUpTo(navController.graph.findStartDestination().id, inclusive = false)
                    .setLaunchSingleTop(true)
                    // default animation 추가
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)

                val graph = navController.currentDestination?.parent
                val destination = graph?.findNode(menuItem.itemId)
                val options = builder.build()
                destination?.id?.let { id ->
                    navController.navigate(id, null, options)
                }

                return@setOnItemSelectedListener true
            }
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