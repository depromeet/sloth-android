package com.depromeet.domain.usecase.login

import com.depromeet.domain.entity.LoginSlothEntity
import com.depromeet.domain.repository.LoginRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchSlothAuthInfoUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke(authToken: String, socialType: String): Flow<Result<LoginSlothEntity>> {
        return loginRepository.fetchSlothAuthInfo(authToken, socialType)
    }
}