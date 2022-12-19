package com.depromeet.sloth.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityHomeBinding
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initViews()
        initNavigation()
    }

    override fun initViews() {
        super.initViews()

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
        navController = navHostFragment.findNavController()

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