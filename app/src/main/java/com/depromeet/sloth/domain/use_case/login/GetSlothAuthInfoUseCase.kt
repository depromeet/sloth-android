package com.depromeet.sloth.domain.use_case.login

import com.depromeet.sloth.common.Result
import com.depromeet.sloth.data.model.response.login.LoginSlothResponse
import com.depromeet.sloth.domain.repository.LoginRepository
import javax.inject.Inject


class GetSlothAuthInfoUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(authToken: String, socialType: String): Result<LoginSlothResponse> {
        return loginRepository.fetchSlothAuthInfo(authToken, socialType)
    }
}