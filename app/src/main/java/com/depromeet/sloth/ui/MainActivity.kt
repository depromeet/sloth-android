package com.depromeet.sloth.ui

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.HealthResponse
import com.depromeet.sloth.data.network.HealthState
import com.depromeet.sloth.databinding.ActivityMainBinding
import com.depromeet.sloth.ui.base.BaseActivity
import kotlinx.coroutines.Dispatchers

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel: MainViewModel
        get() = MainViewModel()

    override fun getViewBinding(): ActivityMainBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainScope {
            viewModel.processHealthWork(Dispatchers.IO).let {
                when (it) {
                    is HealthState.Success<HealthResponse> -> Log.e("Success", "${it.data}")
                    is HealthState.Error -> Log.e("Error", "${it.exception}")
                }
            }
        }
    }
}