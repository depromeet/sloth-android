package com.depromeet.sloth.ui.register

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityRegisterLessonBinding
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterLessonActivity :
    BaseActivity<ActivityRegisterLessonBinding>(R.layout.activity_register_lesson) {

    private val viewModel: RegisterLessonViewModel by viewModels()

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.lesson_register_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.tbRegisterLesson.apply {
            setNavigationOnClickListener {
                if (!navController.navigateUp()) finish() else navController.navigateUp()
            }
        }
    }
}