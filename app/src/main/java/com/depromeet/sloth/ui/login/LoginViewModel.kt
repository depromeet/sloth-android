package com.depromeet.sloth.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.repository.LoginRepository
import com.depromeet.sloth.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
) : ViewModel() {

    private val _loginState = MutableLiveData<Event<Boolean>>()
    val loginState: LiveData<Event<Boolean>>
        get() = _loginState

    init {
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        _loginState.value = Event(loginRepository.checkedLoggedIn())
    }

    suspend fun fetchSlothAuthInfo(accessToken: String, socialType: String) =
        withContext(viewModelScope.coroutineContext) {
            loginRepository.fetchSlothAuthInfo(authToken = accessToken, socialType = socialType)
        }

    suspend fun fetchGoogleAuthInfo(authCode: String) =
        withContext(viewModelScope.coroutineContext) {
            loginRepository.fetchGoogleAuthInfo(authCode = authCode)
        }
}