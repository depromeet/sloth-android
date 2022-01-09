package com.depromeet.sloth

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initMode()
        initSdk()
    }

    private fun initSdk() {
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }

    private fun initMode() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
    }
}