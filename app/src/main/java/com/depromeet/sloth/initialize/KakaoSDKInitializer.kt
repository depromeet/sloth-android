package com.depromeet.sloth.initialize

import android.content.Context
import androidx.startup.Initializer
import com.depromeet.sloth.R
import com.kakao.sdk.common.KakaoSdk

class KakaoSDKInitializer: Initializer<Unit> {

    override fun create(context: Context) {
        KakaoSdk.init(context, context.getString(R.string.kakao_app_key))
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}