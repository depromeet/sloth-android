package com.depromeet.sloth.data.repository

import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.Member
import com.depromeet.sloth.data.network.AccessTokenAuthenticator
import com.depromeet.sloth.data.network.RetrofitServiceGenerator
import com.depromeet.sloth.data.network.member.MemberService
import com.depromeet.sloth.data.network.member.MemberUpdateRequest
import com.depromeet.sloth.data.network.member.MemberUpdateResponse
import com.depromeet.sloth.ui.common.UiState
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
): MemberRepository {

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


    override suspend fun fetchMemberInfo(): UiState<Member> {
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

                        UiState.Success(this.body() ?: Member())
                    }
                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override suspend fun updateMemberInfo(
        memberUpdateRequest: MemberUpdateRequest,
    ): UiState<MemberUpdateResponse> {
        RetrofitServiceGenerator(AccessTokenAuthenticator((preferenceManager)))
            .build(preferenceManager.getAccessToken())
            .create(MemberService::class.java)
            .updateMemberInfo(memberUpdateRequest)?.run {
                return when (this.code()) {
                    200 -> {
                        val newAccessToken = headers()["Authorization"] ?: ""
                        if (newAccessToken.isNotEmpty()) {
                            preferenceManager.updateAccessToken(newAccessToken)
                        }

                        UiState.Success(this.body() ?: MemberUpdateResponse())
                    }
                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
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

    override suspend fun logout(): UiState<String> {
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

                        UiState.Success(this.body() ?: "")
                    }
                    else -> UiState.Error(Exception(message()))
                }
            } ?: return UiState.Error(Exception("Retrofit Exception"))
    }

    override fun removeAuthToken() {
        preferenceManager.removeAuthToken()
    }
}