package com.depromeet.domain.usecase.userauth

import com.depromeet.domain.entity.LoginGoogleEntity
import com.depromeet.domain.repository.UserAuthRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GoogleLoginUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke(authCode: String): Flow<Result<LoginGoogleEntity>> {
        return userAuthRepository.googleLogin(authCode)
    }
}