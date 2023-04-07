package com.depromeet.domain.usecase.userauth

import com.depromeet.domain.repository.UserAuthRepository
import javax.inject.Inject


class DeleteAuthTokenUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    suspend operator fun invoke() {
        userAuthRepository.deleteAuthToken()
    }
}