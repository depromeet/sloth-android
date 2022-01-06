package com.depromeet.sloth.ui.test

import android.os.Bundle
import android.util.Log
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.health.HealthResponse
import com.depromeet.sloth.data.network.health.HealthState
import com.depromeet.sloth.databinding.ActivityTestBinding
import com.depromeet.sloth.ui.base.BaseActivity

class TestActivity : BaseActivity<ActivityTestBinding>() {
    private val viewModel: TestViewModel
        get() = TestViewModel()

    override fun getViewBinding(): ActivityTestBinding = ActivityTestBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainScope {
            viewModel.processHealthWork().let {
                when (it) {
                    is HealthState.Success<HealthResponse> -> Log.e("Success", "${it.data}")
                    is HealthState.Error -> Log.e("Error", "${it.exception}")
                }
            }
        }
    }
}