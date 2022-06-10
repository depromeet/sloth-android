package com.depromeet.sloth.data.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

class AuthenticationInterceptor(
    private val accessToken: String? = null
) : Interceptor {
    private val contentType = "Content-Type"
    private val contentTypeValue = "application/json"

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        var builder: Request.Builder = original.newBuilder()
        builder = builder
            .header(contentType, contentTypeValue)
            .apply {
                accessToken?.run {
                    header("Authorization", this)
                } ?: Timber.tag("AuthenticationInterceptor").e("token is empty")
            }

        val request: Request = builder.build()

        return chain.proceed(request)
    }
}