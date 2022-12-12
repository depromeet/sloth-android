package com.depromeet.sloth.ui.register

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityRegisterLessonBinding
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterLessonActivity :
    BaseActivity<ActivityRegisterLessonBinding>(R.layout.activity_register_lesson) {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavigation()
        initListener()
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.lesson_register_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun initListener() = with(binding) {
        tbRegisterLesson.apply {
            setNavigationOnClickListener {
                if (!navController.navigateUp()) {
                    finish()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()
}