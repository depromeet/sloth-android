package com.depromeet.domain.usecase.userauth

import com.depromeet.domain.entity.LoginSlothEntity
import com.depromeet.domain.repository.UserAuthRepository
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SlothLoginUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke(authToken: String, socialType: String): Flow<Result<LoginSlothEntity>> {
        return userAuthRepository.slothLogin(authToken, socialType)
    }
}