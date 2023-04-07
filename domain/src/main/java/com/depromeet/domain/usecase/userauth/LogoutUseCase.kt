package com.depromeet.domain.usecase.userauth

import com.depromeet.domain.repository.UserAuthRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class LogoutUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke() : Flow<Result<String>> {
        return userAuthRepository.logout()
    }
}