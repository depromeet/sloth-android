package com.depromeet.sloth.data.network

import com.depromeet.sloth.data.PreferenceManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject


/**
 * @author 최철훈
 * @created 2022-05-11
 * @desc
 */
class AccessTokenAuthenticator @Inject constructor(
    private val preferenceManager: PreferenceManager
) : Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = " "
        if (isRequestWithAccessToken(response).not()) return null

        synchronized (this) {
            val newAccessToken = " "
            if (accessToken != newAccessToken) {
                return newRequestWithAccessToken(response.request, newAccessToken);
            }

            val refreshToken = " "
            return newRequestWithAccessToken(response.request, refreshToken);
        }
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header("Authorization")
        return header != null && header.startsWith("Bearer")
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}