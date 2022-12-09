package com.depromeet.sloth.initialize

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer

class NightModeInitializer: Initializer<Unit> {

    override fun create(context: Context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}