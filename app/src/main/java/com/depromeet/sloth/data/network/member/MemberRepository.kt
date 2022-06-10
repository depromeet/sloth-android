package com.depromeet.sloth.data.network.member

interface MemberRepository {

    suspend fun fetchMemberInfo(): MemberState<MemberInfoResponse>

    suspend fun updateMemberInfo(
        memberUpdateInfoRequest: MemberUpdateInfoRequest,
    ): MemberUpdateState<MemberUpdateInfoResponse>

    suspend fun logout(): MemberLogoutState<String>

    fun removeAuthToken()

    fun putFCMToken(fcmToken: String)

    fun getFCMToken(): String
}