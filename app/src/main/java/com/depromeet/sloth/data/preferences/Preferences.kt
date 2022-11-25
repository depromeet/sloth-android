package com.depromeet.sloth.data.preferences


interface Preferences {

    fun updateAccessToken(accessToken: String)

    fun saveAuthToken(accessToken: String, refreshToken: String)

    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun removeAuthToken()

    fun saveFCMToken(fcmToken: String)

    fun getFCMToken(): String

    fun removeFCMToken()

    companion object {
        const val KEY_ACCESS_TOKEN = "accessToken"
        const val KEY_REFRESH_TOKEN = "refreshToken"
        const val KEY_FCM_TOKEN = "fcmToken"
    }
}