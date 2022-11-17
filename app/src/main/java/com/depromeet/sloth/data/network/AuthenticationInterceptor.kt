package com.depromeet.sloth.data.network

import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import com.depromeet.sloth.util.KEY_CONTENT_TYPE
import com.depromeet.sloth.util.VALUE_CONTENT_TYPE
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class AuthenticationInterceptor @Inject constructor(
    private val preferences: Preferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        var builder: Request.Builder = original.newBuilder()
        builder = builder
            .header(KEY_CONTENT_TYPE,  VALUE_CONTENT_TYPE)
            .header(KEY_AUTHORIZATION, preferences.getAccessToken())
        val request: Request = builder.build()
        return chain.proceed(request)
    }
}