//package com.depromeet.sloth.data.preferences
//
//import android.content.SharedPreferences
//
//
//class PreferenceImpl(
//    private val sharedPref: SharedPreferences,
//    private val dataStore: DataStore<Preference>
//) : Preference {
//
//    override fun updateAccessToken(accessToken: String) {
//        sharedPref.edit()
//            .putString(Preferences.KEY_ACCESS_TOKEN, accessToken)
//            .apply()
//    }
//
//    override fun saveAuthToken(accessToken: String, refreshToken: String) {
//        sharedPref.edit()
//            .putString(Preferences.KEY_ACCESS_TOKEN, accessToken)
//            .putString(Preferences.KEY_REFRESH_TOKEN, refreshToken)
//            .apply()
//    }
//
//    override fun getAccessToken(): String {
//        return sharedPref.getString(Preferences.KEY_ACCESS_TOKEN, DEFAULT_STRING_VALUE)
//            ?: DEFAULT_STRING_VALUE
//    }
//
//    override fun getRefreshToken(): String {
//        return sharedPref.getString(Preferences.KEY_REFRESH_TOKEN, DEFAULT_STRING_VALUE)
//            ?: DEFAULT_STRING_VALUE
//    }
//
//    fun deleteAuthToken() {
//        sharedPref.edit()
//            .remove(Preferences.KEY_ACCESS_TOKEN)
//            .remove(Preferences.KEY_REFRESH_TOKEN)
//            .apply()
//    }
//
//    override suspend fun saveAuthToken(accessToken: String, refreshToken: String) {
//        dataStore.edit { prefs ->
//            prefs[ACCESS_TOKEN] = accessToken
//            prefs[REFRESH_TOKEN] = refreshToken
//        }
//    }
//
//    override fun getAccessToken() = dataStore.data
//        .catch { exception ->
//            if (exception is IOException) {
//                exception.printStackTrace()
//                emit(emptyPreferences())
//            } else {
//                throw exception
//            }
//        }
//
//        .map { prefs ->
//            prefs[ACCESS_TOKEN] ?: DEFAULT_STRING_VALUE
//        }
//
//    override fun getRefreshToken() = dataStore.data
//        .catch { exception ->
//            if (exception is IOException) {
//                exception.printStackTrace()
//                emit(emptyPreferences())
//            } else {
//                throw exception
//            }
//        }
//
//        .map { prefs ->
//            prefs[REFRESH_TOKEN] ?: DEFAULT_STRING_VALUE
//        }
//
//    override suspend fun updateAccessToken(accessToken: String) {
//        dataStore.edit { prefs ->
//            prefs[ACCESS_TOKEN] = accessToken
//        }
//    }
//
//    suspend fun deleteAuthToken() {
//        dataStore.edit { prefs ->
//            prefs.remove(ACCESS_TOKEN)
//            prefs.remove(REFRESH_TOKEN)
//        }
//    }
//
//    companion object {
//        // key 로 string 을 사용 하는 spf 와 다르게 type-safe 를 위해 preferencesKey 를 사용
//        // 저장할 type 이 string 이기 때문에 stringPreferencesKey
//        val ACCESS_TOKEN = stringPreferencesKey(KEY_ACCESS_TOKEN)
//        val REFRESH_TOKEN = stringPreferencesKey(KEY_REFRESH_TOKEN)
//    }
//}