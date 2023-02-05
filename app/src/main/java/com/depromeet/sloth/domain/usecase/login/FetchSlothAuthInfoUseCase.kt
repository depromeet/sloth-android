package com.depromeet.sloth.domain.usecase.login

import com.depromeet.sloth.util.Result
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchSlothAuthInfoUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke(authToken: String, socialType: String): Flow<Result<LoginSlothResponse>> {
        return loginRepository.fetchSlothAuthInfo(authToken, socialType)
    }
}