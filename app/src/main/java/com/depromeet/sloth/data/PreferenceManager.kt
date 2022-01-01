package com.depromeet.sloth.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 데이터 저장 및 로드 클래스
 */
class PreferenceManager(
    private val context: Context
) {
    companion object {
        private const val PREFERENCES_NAME = "Sloth-pref"
        private const val DEFAULT_STRING_VALUE = ""
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private val prefs by lazy { getPreferences(context) }

    private val editor by lazy { prefs.edit() }

    /**
     * 모든 저장 데이터 삭제
     */
    fun clear() {
        editor.clear()
        editor.apply()
    }

    fun updateAccessToken(accessToken: String) {
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.apply()
    }

    /**
     * accessToken, refreshToken (인증 토큰) 저장
     *
     * @param accessToken
     * @param refreshToken
     */
    fun putAuthToken(accessToken: String, refreshToken: String) {
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.apply()
    }

    /**
     * 저장한 accessToken 로드
     *
     * @return
     */
    fun getAccessToken(): String {
        return prefs.getString(ACCESS_TOKEN, DEFAULT_STRING_VALUE) ?: DEFAULT_STRING_VALUE
    }

    /**
     * 저장한 refreshToken 로드
     *
     * @return
     */
    fun getRefreshToken(): String {
        return prefs.getString(REFRESH_TOKEN, DEFAULT_STRING_VALUE) ?: DEFAULT_STRING_VALUE
    }

    /**
     * 저장된 AccessToken, RefreshToken (인증 토큰) 삭제
     *
     */
    fun removeAuthToken() {
        editor.remove(ACCESS_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.apply()
    }
}