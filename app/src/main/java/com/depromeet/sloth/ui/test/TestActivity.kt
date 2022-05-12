package com.depromeet.sloth.ui.test

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import com.depromeet.sloth.data.network.health.HealthResponse
import com.depromeet.sloth.data.network.health.HealthState
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.databinding.ActivityTestBinding
import com.depromeet.sloth.extensions.handleLoadingState
import com.depromeet.sloth.extensions.showLogoutDialog
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : BaseActivity<ActivityTestBinding>() {
    private val viewModel: TestViewModel by viewModels()

    override fun getActivityBinding(): ActivityTestBinding = ActivityTestBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainScope {
//            viewModel.processHealthWork().let {
//                when (it) {
//                    is HealthState.Success<HealthResponse> -> Log.e("Success", "${it.data}")
//                    is HealthState.Error -> Log.e("Error", "${it.exception}")
//                }
//            }

            viewModel.fetchTodayLessonList().let {
                when (it) {
                    is LessonState.Loading -> {
                        Log.d("Success", "Loading")
                    }

                    is LessonState.Success<List<LessonTodayResponse>> -> {
                        Log.d("Success", "${it.data}")
                        //setLessonList(it.data)
                    }
                    is LessonState.Unauthorized -> {
                        Log.d("Success", "Unauthorized")
                    }

                    is LessonState.NotFound -> {
                        Log.d("Error", "NotFound")
                    }

                    is LessonState.Forbidden -> {
                        Log.d("Error", "Forbidden")
                    }

                    is LessonState.Error -> {
                        Log.d("Error", "${it.exception}")
                    }
                }
            }
        }
    }
}