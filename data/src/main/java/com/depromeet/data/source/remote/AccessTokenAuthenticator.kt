package com.depromeet.data.source.remote

import com.depromeet.data.source.local.preferences.PreferenceManager
import com.depromeet.data.util.KEY_AUTHORIZATION
import com.depromeet.data.util.KEY_CONTENT_TYPE
import com.depromeet.data.util.VALUE_CONTENT_TYPE
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject


/**
 * @author 최철훈
 * @created 2022-05-11
 * @desc
 * Response Code가 401인 상태일 때 authenticate() 메서드를 실행
 * 나나공 백엔드 설계대로는 처음 401 코드를 받았을 때 Access Token을 내려주지 않고, Refresh Token으로 재요청 해야 함
 * *** authenticate() 메서드 실행시 Response에 AccessToken 필드가 포함되어 있지 않으면 처음으로 401 응답 코드를 받은 상태 ***
 */
class AccessTokenAuthenticator @Inject constructor(
    private val preferences: PreferenceManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = runBlocking {
            preferences.getAccessToken().first()
        }

        if (hasNotAccessTokenOnResponse(response)) {
            synchronized(this) {
                val newAccessToken = runBlocking {
                    preferences.getAccessToken().first()
                }
                if (accessToken != newAccessToken) {
                    return newRequestWithAccessToken(response.request, newAccessToken)
                }

                val refreshToken = runBlocking {
                    preferences.getRefreshToken().first()
                }
                return newRequestWithAccessToken(response.request, refreshToken)
            }
        }

        return null
    }

    private fun hasNotAccessTokenOnResponse(response: Response): Boolean =
        response.header(KEY_AUTHORIZATION) == null

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request =
        request.newBuilder()
            .header(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE)
            .header(KEY_AUTHORIZATION, accessToken)
            .build()
}