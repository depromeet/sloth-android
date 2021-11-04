package com.depromeet.sloth.data.db

import android.content.Context
import android.content.SharedPreferences

/**
 * 데이터 저장 및 로드 클래스
 */
class PreferenceManager(
    private val context: Context
) {

    companion object {
        const val PREFERENCES_NAME = "Sloth-pref"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f

        const val ID_TOKEN = "idToken"
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private val prefs by lazy { getPreferences(context) }

    private val editor by lazy { prefs.edit() }

    /**
     * String 값 저장
     * @param key
     * @param value
     */
    fun setString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * boolean 값 저장
     * @param key
     * @param value
     */
    fun setBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * int 값 저장
     * @param key
     * @param value
     */
    fun setInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * long 값 저장
     * @param key
     * @param value
     */
    fun setLong(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * float 값 저장
     * @param key
     * @param value
     */
    fun setFloat(key: String?, value: Float) {
        editor.putFloat(key, value)
        editor.apply()
    }

    /**
     * String 값 로드
     * @param key
     * @return
     */
    fun getString(key: String?): String? {
        return prefs.getString(key, DEFAULT_VALUE_STRING)
    }

    /**
     * boolean 값 로드
     * @param key
     * @return
     */
    fun getBoolean(key: String?): Boolean {
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /**
     * int 값 로드
     * @param key
     * @return
     */
    fun getInt(key: String?): Int {
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    /**
     * long 값 로드
     * @param key
     * @return
     */
    fun getLong(key: String?): Long {
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    /**
     * float 값 로드
     * @param key
     * @return
     */
    fun getFloat(key: String?): Float {
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    /**
     * 키 값 삭제
     * @param key
     */
    fun removeKey(key: String?) {
        editor.remove(key)
        editor.apply()
    }

    /**
     * 모든 저장 데이터 삭제
     */
    fun clear() {
        editor.clear()
        editor.apply()
    }

    /**
     * accessToken 저장
     * @param accessToken
     */
    fun putAccessToken(accessToken: String) {
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
    fun getAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN, null)
    }
}