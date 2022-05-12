package com.depromeet.sloth.data.network

import android.util.Log
import com.depromeet.sloth.data.PreferenceManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.lang.Exception
import javax.inject.Inject


/**
 * @author 최철훈
 * @created 2022-05-11
 * @desc
 */
class AccessTokenAuthenticator @Inject constructor(
    private val preferenceManager: PreferenceManager
) : Authenticator {
    private val contentType = "Content-Type"
    private val contentTypeValue = "application/json"
    private var limitCount = 0

    //401인 상태에서만 해당 콜백 메서드를 실행함
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = preferenceManager.getAccessToken()
        Log.e("test", response.headers.toString())

        //우리 스펙은 처음 401이 떨어졌을 때 응답으로 Access Token을 내려주지 않음
        //응답에서 AccessToken을 받지않으면 처음 요청을 보낸 상태
        if (hasNotAccessTokenOnResponse(response)) {
            synchronized (this) {
                try {
                    val newAccessToken = preferenceManager.getAccessToken()
                    if (accessToken != newAccessToken) {
                        return newRequestWithAccessToken(response.request, newAccessToken)
                    }

                    val refreshToken = preferenceManager.getRefreshToken()
                    Log.e("test", "123".toString())
                    limitCount++
                    if(limitCount > 20) return null
                    return newRequestWithAccessToken(response.request, "")
                } catch (exception: Exception) {
                    return null
                }
            }
        }

        return null
    }

    private fun hasNotAccessTokenOnResponse(response: Response): Boolean =
        response.header("Authorization") == null

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header(contentType, contentTypeValue)
            .header("Authorization", accessToken)
            .build()
    }
}