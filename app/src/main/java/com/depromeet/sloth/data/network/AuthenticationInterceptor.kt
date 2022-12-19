package com.depromeet.sloth.data.network

import com.depromeet.sloth.data.preferences.PreferenceManager
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import com.depromeet.sloth.util.KEY_CONTENT_TYPE
import com.depromeet.sloth.util.VALUE_CONTENT_TYPE
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class AuthenticationInterceptor @Inject constructor(
    private val preferences: PreferenceManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            preferences.getAccessToken().first()
        }
        val request: Request = chain.request().newBuilder()
            .addHeader(KEY_CONTENT_TYPE,  VALUE_CONTENT_TYPE)
            .addHeader(KEY_AUTHORIZATION, accessToken)
            .build()
        return chain.proceed(request)
    }
}