package com.depromeet.data.service

import com.depromeet.data.model.response.userprofile.UserProfileResponse
import com.depromeet.data.model.request.userprofile.UserProfileRequest
import com.depromeet.data.model.response.userprofile.UserProfileUpdateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


interface UserProfileService {
    @GET("api/member")
    suspend fun fetchUserProfile(): Response<UserProfileResponse>?

    //TODO userProfileRquest와 profileImage를 파라미터에 담아서 통신, @Multipart
    @Multipart
    @PATCH("api/member")
    suspend fun updateUserProfile(
        @Part("userProfileRequest") userProfileRequest: UserProfileRequest,
        @Part profileImageUrl: MultipartBody.Part?
    ): Response<UserProfileUpdateResponse>?
}