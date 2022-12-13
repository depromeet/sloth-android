//package com.depromeet.sloth.data.preferences
//
//import android.content.SharedPreferences
//import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
//
//
//class PreferencesImpl(
//    private val sharedPref: SharedPreferences
//) : Preferences {
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
//    override fun removeAuthToken() {
//        sharedPref.edit()
//            .remove(Preferences.KEY_ACCESS_TOKEN)
//            .remove(Preferences.KEY_REFRESH_TOKEN)
//            .apply()
//    }
//}