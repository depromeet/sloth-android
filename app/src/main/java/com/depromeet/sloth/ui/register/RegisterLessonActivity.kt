package com.depromeet.sloth.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityRegisterLessonBinding
import com.depromeet.sloth.navigation.KeepStateNavigator
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterLessonActivity : BaseActivity<ActivityRegisterLessonBinding>(R.layout.activity_register_lesson) {

    private val viewModel: RegisterLessonViewModel by viewModels()
    lateinit var navController: NavController

    companion object {
        fun newIntent(activity: Activity) =
            Intent(activity, RegisterLessonActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_register_container) as NavHostFragment
        navController = navHostFragment.navController

        val navigator = KeepStateNavigator(this,
            navHostFragment.childFragmentManager,
            R.id.nav_host_register_container)
        navController.navigatorProvider.addNavigator(navigator)
        navController.setGraph(R.navigation.nav_graph_register_lesson)

        binding.tbRegisterLesson.setNavigationOnClickListener { navController.navigateUp() }
    }
}