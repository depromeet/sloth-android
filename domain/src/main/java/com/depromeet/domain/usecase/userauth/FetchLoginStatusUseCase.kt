package com.depromeet.domain.usecase.userauth

import com.depromeet.domain.repository.UserAuthRepository
import javax.inject.Inject


class FetchLoginStatusUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return userAuthRepository.fetchLoginStatus()
    }
}