package com.depromeet.sloth.domain.use_case.login

import com.depromeet.sloth.domain.repository.LoginRepository
import javax.inject.Inject

class CheckLoggedInUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    operator fun invoke(): Boolean {
        return loginRepository.checkLoggedIn()
    }
}