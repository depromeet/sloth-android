package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.data.model.Member

interface MemberRepository {

    suspend fun fetchMemberInfo(): MemberState<Member>

    suspend fun updateMemberInfo(
        memberUpdateInfoRequest: MemberUpdateInfoRequest,
    ): MemberUpdateState<MemberUpdateInfoResponse>

    suspend fun logout(): MemberLogoutState<String>

    fun removeAuthToken()

    fun putFCMToken(fcmToken: String)

    fun getFCMToken(): String
}