package com.depromeet.sloth.data.network

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.util.KEY_AUTHORIZATION
import com.depromeet.sloth.util.KEY_CONTENT_TYPE
import com.depromeet.sloth.util.VALUE_CONTENT_TYPE
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
    private val preferenceManager: PreferenceManager
) : Authenticator {
    private val keyContentType =  KEY_CONTENT_TYPE
    private val valueContentType = VALUE_CONTENT_TYPE
    private val keyAuthorization = KEY_AUTHORIZATION
    private var retryLimitCount = 1

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = preferenceManager.getAccessToken()

        if (hasNotAccessTokenOnResponse(response)) {
            synchronized(this) {
                val newAccessToken = preferenceManager.getAccessToken()
                if (accessToken != newAccessToken) {
                    return newRequestWithAccessToken(response.request, newAccessToken)
                }

                if (retryLimitCount == 0) {
                    return null
                }
                retryLimitCount = retryLimitCount.minus(1)

                val refreshToken = preferenceManager.getRefreshToken()
                return newRequestWithAccessToken(response.request, refreshToken)
            }
        }

        return null
    }

    private fun hasNotAccessTokenOnResponse(response: Response): Boolean =
        response.header("Authorization") == null

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request =
        request.newBuilder()
            .header(keyContentType, valueContentType)
            .header(keyAuthorization, accessToken)
            .build()
}