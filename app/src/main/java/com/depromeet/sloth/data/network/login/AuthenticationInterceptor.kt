package com.depromeet.sloth.data.network.login

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * HTTP 통신에 사용하는 Request 객체를 생성
 * 생성한 Request의 헤더에는 Token을 담은 Authorization 옵션이 추가되어 있음
 *
 * @property accessToken
 */

class AuthenticationInterceptor(private val accessToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        val builder: Request.Builder = original.newBuilder()
            .header("Authorization", accessToken)

        val request: Request = builder.build()

        return chain.proceed(request)
    }
}