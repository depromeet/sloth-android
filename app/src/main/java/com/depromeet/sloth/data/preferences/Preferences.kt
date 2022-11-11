package com.depromeet.sloth.data.preferences


interface Preferences {

    fun updateAccessToken(accessToken: String)

    fun saveAuthToken(accessToken: String, refreshToken: String)

    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun removeAuthToken()

    companion object {
        const val KEY_ACCESS_TOKEN = "accessToken"
        const val KEY_REFRESH_TOKEN = "refreshToken"
    }
}