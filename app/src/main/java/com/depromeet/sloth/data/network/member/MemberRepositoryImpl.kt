package com.depromeet.sloth.data.network.member

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
):MemberRepository {
//    fun fetchMemberInfo(): Flow<UIState<Member>> = flow<UIState<Member>> {
//
//        emit(UIState.Loading)
//
//        val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//            .build(preferenceManager.getAccessToken())
//            .create(MemberService::class.java)
//            .fetchMemberInfo() ?: run {
//            emit(UIState.Error(Exception("Response is null")))
//            return@flow
//        }
//
//        when (response.code()) {
//            200 -> {
//                val newAccessToken = response.headers()["Authorization"] ?: ""
//                if (newAccessToken.isNotEmpty()) {
//                    preferenceManager.updateAccessToken(newAccessToken)
//                }
//                Timber.tag("fetch Success").d("${response.body()}")
//                emit(UIState.Success(response.body() ?: Member()))
//            }
//            401 -> {
//                preferenceManager.removeAuthToken()
//                emit(UIState.Error(Exception(response.message())))
//            }
//            else -> emit(UIState.Error(Exception(response.message())))
//        }
//    }
    //.catch { throwable -> emit(UIState.Error(throwable)) }
    // onCompletion 으로 인해 collectLatest에서 가장 마지막 값으로 null을 반환함
    //.onCompletion { emit(UIState.UnLoading) }


    override suspend fun fetchMemberInfo(): MemberState<MemberInfoResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .fetchMemberInfo()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        MemberState.Success(this.body() ?: MemberInfoResponse())
                    }
                    else -> MemberState.Error(Exception(message()))
                }
            } ?: return MemberState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun updateMemberInfo(
        memberUpdateInfoRequest: MemberUpdateInfoRequest,
    ): MemberUpdateState<MemberUpdateInfoResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .updateMemberInfo(memberUpdateInfoRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        MemberUpdateState.Success(this.body() ?: MemberUpdateInfoResponse())
                    }
                    else -> MemberUpdateState.Error(Exception(message()))
                }
            } ?: return MemberUpdateState.Error(Exception("Retrofit Exception"))
    }

//    fun updateMemberInfo(memberUpdateInfoRequest: MemberUpdateInfoRequest): Flow<UIState<MemberUpdateInfoResponse>> =
//        flow {
//            emit(UIState.Loading)
//
//            val response = RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
//                .build(preferenceManager.getAccessToken())
//                .create(MemberService::class.java)
//                .updateMemberInfo(memberUpdateInfoRequest) ?: run {
//                emit(UIState.Error(Exception("Response is null")))
//                return@flow
//            }
//            when (response.code()) {
//                200 -> {
//                    val newAccessToken = response.headers()["Authorization"] ?: ""
//                    if (newAccessToken.isNotEmpty()) {
//                        preferenceManager.updateAccessToken(newAccessToken)
//                    }
//
//                    emit(UIState.Success(response.body() ?: MemberUpdateInfoResponse.Empty))
//                }
//                401 -> {
//                    preferenceManager.removeAuthToken()
//                    emit(UIState.Error(Exception(response.message())))
//                }
//
//                else -> emit(UIState.Error(Exception(response.message())))
//            }
//        }
//            .catch { throwable -> emit(UIState.Error(throwable)) }
//            .onCompletion { emit(UIState.UnLoading) }

    override suspend fun logout(): MemberLogoutState<String> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .logout()?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        MemberLogoutState.Success(this.body() ?: "")
                    }
                    else -> MemberLogoutState.Error(Exception(message()))
                }
            } ?: return MemberLogoutState.Error(Exception("Retrofit Exception"))
    }

    override fun removeAuthToken() {
        preferenceManager.removeAuthToken()
    }

    override fun putFCMToken(fcmToken: String) {
        preferenceManager.putFCMToken(fcmToken)
    }

    override fun getFCMToken(): String {
        return preferenceManager.getFCMToken()
    }
}