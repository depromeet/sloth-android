package com.depromeet.sloth.ui.test

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.lesson.list.LessonState
import com.depromeet.sloth.data.network.lesson.list.LessonTodayResponse
import com.depromeet.sloth.databinding.ActivityTestBinding
import com.depromeet.sloth.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : BaseActivity<ActivityTestBinding>(R.layout.activity_test) {
    private val viewModel: TestViewModel by viewModels()

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

                    is LessonState.Error -> {
                        Log.d("Error", "${it.throwable}")
                    }
                }
            }
        }
    }
}