package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.BuildConfig
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.data.network.ServiceGenerator.createService
import java.lang.Exception

class MemberInfoRepository {
    suspend fun fetchMemberInfo(accessToken: String): MemberInfoState<MemberInfoResponse> {
        ServiceGenerator.setBuilderOptions(
            targetUrl = BuildConfig.SLOTH_BASE_URL,
            authToken = accessToken
        ).createService(
            serviceClass = MemberInfoService::class.java
        ).fetchMemberInfo()?.run {
            return MemberInfoState.Success(
                this.body() ?: MemberInfoResponse()
            )
        } ?: return MemberInfoState.Error(Exception("Retrofit Exception"))
    }
}