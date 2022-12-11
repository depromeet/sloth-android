package com.depromeet.sloth.domain.use_case.login

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.domain.repository.LoginRepository
import javax.inject.Inject

class GetGoogleAuthInfoUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(authCode: String): Result<LoginGoogleResponse> {
        return loginRepository.fetchGoogleAuthInfo(authCode)
    }
}