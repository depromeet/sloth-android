package com.depromeet.sloth.presentation

import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityMainBinding
import com.depromeet.sloth.presentation.base.BaseActivity
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun preload() {
        installSplashScreen()
    }

    override fun init() {
        initNavigation()
    }

    private fun initNavigation() = with(binding) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_home_container) as NavHostFragment

        val navController = navHostFragment.findNavController()

        bnvMain.apply {
            setupWithNavController(navController)
            itemIconTintList = null

        }

        // BottomNavigationView radius 설정
        val radius = resources.getDimension(R.dimen.corner_radius)
        val bottomNavigationViewBackground = bnvMain.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .build()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.today_lesson, R.id.lesson_list, R.id.manage, R.id.finish_lesson_dialog,
                    R.id.wait_dialog, R.id.logout_dialog, R.id.update_member ->
                    bnvMain.visibility = View.VISIBLE

                else -> bnvMain.visibility = View.GONE
            }
        }
    }
}