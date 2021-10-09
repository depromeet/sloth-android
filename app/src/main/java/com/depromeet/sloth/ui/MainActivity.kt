package com.depromeet.sloth.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.depromeet.sloth.R
import com.depromeet.sloth.data.network.HealthRepository
import com.depromeet.sloth.data.network.HealthResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = HealthRepository()

        CoroutineScope(Dispatchers.Main).launch {
            var response: Response<HealthResponse>? = null
            val job = CoroutineScope(Dispatchers.IO).launch {
                response = repository.getHealth()
            }

            job.join()

            when(response?.isSuccessful) {
                true -> Log.e("${response?.code()}", "${response?.body()}")
                false -> Log.e("${response?.code()}", "${response?.body()}")
            }
        }
    }
}