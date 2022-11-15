package com.depromeet.sloth.data.network

import com.depromeet.sloth.data.preferences.Preferences
import com.depromeet.sloth.util.KEY_CONTENT_TYPE
import com.depromeet.sloth.util.VALUE_CONTENT_TYPE
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
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
    private val preferences: Preferences
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = preferences.getAccessToken()
        Timber.tag("accessToken").d(accessToken)

        if (hasNotAccessTokenOnResponse(response)) {
            synchronized(this) {
                val newAccessToken = preferences.getAccessToken()
                if (accessToken != newAccessToken) {
                    Timber.tag("newAccessToken").d(newAccessToken)
                    return newRequestWithAccessToken(response.request, newAccessToken)
                }

                val refreshToken = preferences.getRefreshToken()
                Timber.tag("refreshToken").d(refreshToken)
                return newRequestWithAccessToken(response.request, refreshToken)
            }
        }

        return null
    }

    private fun hasNotAccessTokenOnResponse(response: Response): Boolean =
        response.header("Authorization") == null

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request =
        request.newBuilder()
            .header(KEY_CONTENT_TYPE, VALUE_CONTENT_TYPE)
            .header("Authorization", accessToken)
            .build()
}