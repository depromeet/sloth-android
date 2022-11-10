package com.depromeet.sloth

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SlothApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initMode()
        initSdk()

    }

    private fun initSdk() {
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }

    private fun initMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
    }
}