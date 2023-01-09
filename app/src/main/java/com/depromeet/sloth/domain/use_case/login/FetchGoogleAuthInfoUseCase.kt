package com.depromeet.sloth.domain.use_case.login

import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.login.LoginGoogleResponse
import com.depromeet.sloth.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchGoogleAuthInfoUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke(authCode: String): Flow<Result<LoginGoogleResponse>> {
        return loginRepository.fetchGoogleAuthInfo(authCode)
    }
}