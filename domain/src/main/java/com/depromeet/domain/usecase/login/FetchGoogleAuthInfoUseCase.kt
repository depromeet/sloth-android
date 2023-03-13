package com.depromeet.domain.usecase.login

import com.depromeet.domain.entity.LoginGoogleEntity
import com.depromeet.domain.repository.LoginRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchGoogleAuthInfoUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke(authCode: String): Flow<Result<LoginGoogleEntity>> {
        return loginRepository.fetchGoogleAuthInfo(authCode)
    }
}