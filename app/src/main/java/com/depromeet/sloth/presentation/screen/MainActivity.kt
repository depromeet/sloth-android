package com.depromeet.sloth.presentation.screen

import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityMainBinding
import com.depromeet.sloth.presentation.screen.base.BaseActivity
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun preload() {
        installSplashScreen()
    }

    override fun init() {
        setSplashScreen()
        initNavigation()
    }

    private fun setSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1f, 0f).run {
                    interpolator = AnticipateInterpolator()
                    duration = 200L
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            }
        }
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
                R.id.wait_dialog, R.id.logout_dialog, R.id.update_member_dialog, R.id.on_boarding_click_plus_dialog, R.id.on_boarding_check_detail_dialog, R.id.on_boarding_register_lesson_dialog ->
                    bnvMain.visibility = View.VISIBLE

                else -> bnvMain.visibility = View.GONE
            }
        }
    }
}