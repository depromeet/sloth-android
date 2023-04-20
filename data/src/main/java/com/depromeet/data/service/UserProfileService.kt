package com.depromeet.data.service

import com.depromeet.data.model.response.userprofile.UserProfileResponse
import com.depromeet.data.model.request.userprofile.UserProfileRequest
import com.depromeet.data.model.response.userprofile.UserProfileUpdateResponse
import retrofit2.Response
import retrofit2.http.*


interface UserProfileService {
    @GET("api/member")
    suspend fun fetchUserProfile(): Response<UserProfileResponse>?

    @PATCH("api/member")
    suspend fun updateUserProfile(
        @Body userProfileRequest: UserProfileRequest
    ): Response<UserProfileUpdateResponse>?
}