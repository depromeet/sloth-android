package com.depromeet.sloth.domain.usecase.login

import com.depromeet.sloth.domain.repository.LoginRepository
import javax.inject.Inject

class FetchLoginStatusUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(): Boolean {
        return loginRepository.fetchLoginStatus()
    }
}