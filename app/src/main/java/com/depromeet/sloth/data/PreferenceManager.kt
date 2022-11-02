package com.depromeet.sloth.data

import android.content.Context
import android.content.SharedPreferences
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private val prefs by lazy { getPreferences(context) }

    private val editor by lazy { prefs.edit() }

    fun updateAccessToken(accessToken: String) {
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.apply()
    }

    fun putAuthToken(accessToken: String, refreshToken: String) {
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.apply()
    }

    fun getAccessToken(): String {
        return prefs.getString(ACCESS_TOKEN, DEFAULT_STRING_VALUE) ?: DEFAULT_STRING_VALUE
    }

    fun getRefreshToken(): String {
        return prefs.getString(REFRESH_TOKEN, DEFAULT_STRING_VALUE) ?: DEFAULT_STRING_VALUE
    }

    fun removeAuthToken() {
        editor.remove(ACCESS_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "Sloth-pref"
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
    }
}