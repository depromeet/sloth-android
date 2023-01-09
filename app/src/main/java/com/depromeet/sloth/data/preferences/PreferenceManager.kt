package com.depromeet.sloth.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.depromeet.sloth.util.KEY_PREFERENCES
import com.depromeet.sloth.util.DEFAULT_STRING_VALUE
import com.depromeet.sloth.util.KEY_ACCESS_TOKEN
import com.depromeet.sloth.util.KEY_REFRESH_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = KEY_PREFERENCES)

class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveAuthToken(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    fun getAccessToken() = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

        .map { prefs ->
            prefs[ACCESS_TOKEN] ?: DEFAULT_STRING_VALUE
        }

    fun getRefreshToken() = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

        .map { prefs ->
            prefs[REFRESH_TOKEN] ?: DEFAULT_STRING_VALUE
        }

    suspend fun updateAccessToken(accessToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun deleteAuthToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
        }
    }

    companion object {
        // key 로 string 을 사용 하는 spf 와 다르게 type-safe 를 위해 preferencesKey 를 사용
        // 저장할 type 이 string 이기 때문에 stringPreferencesKey
        val ACCESS_TOKEN = stringPreferencesKey(KEY_ACCESS_TOKEN)
        val REFRESH_TOKEN = stringPreferencesKey(KEY_REFRESH_TOKEN)
    }
}