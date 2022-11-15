package com.depromeet.sloth.data.network

import com.depromeet.sloth.util.KEY_CONTENT_TYPE
import com.depromeet.sloth.util.VALUE_CONTENT_TYPE
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthenticationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        var builder: Request.Builder = original.newBuilder()
        builder = builder
            .header(KEY_CONTENT_TYPE,  VALUE_CONTENT_TYPE)
        val request: Request = builder.build()
        return chain.proceed(request)
    }
}