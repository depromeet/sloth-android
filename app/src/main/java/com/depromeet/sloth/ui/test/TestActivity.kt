package com.depromeet.sloth.ui.test

import android.os.Bundle
import androidx.activity.viewModels
import com.depromeet.sloth.R
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
//                    is HealthState.Success -> Timber.tag("Success").d("${it.data}")
//                    is HealthState.Error -> Timber.tag("Error").d(it.exception)
//                }
//            }
//
//            viewModel.fetchTodayLessonList().let {
//                when (it) {
//                    is LessonState.Loading -> {
//                        Timber.d("Loading")
//                    }
//
//                    is LessonState.Success<List<LessonTodayResponse>> -> {
//                        Timber.tag("Success").d("${it.data}")
//                        //setLessonList(it.data)
//                    }
//
//                    is LessonState.Error -> {
//                        Timber.tag("Error").d(it.throwable)
//                    }
//                }
//            }
        }
    }
}