package com.depromeet.sloth.data.network

import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class AuthenticationInterceptor(
    private val accessToken: String
) : Interceptor {
    private val contentType = "Content-Type"
    private val contentTypeValue = "application/json"

    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val builder: Request.Builder = original.newBuilder()
            .header("Authorization", accessToken)
            .header(contentType, contentTypeValue)
            .method(original.method, original.body)
        val request: Request = builder.build()

        FormBody
        return chain.proceed(request)
    }
}