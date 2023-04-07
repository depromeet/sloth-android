package com.depromeet.presentation.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.userprofile.UpdateUserProfileUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.extensions.getMutableStateFlow
import com.depromeet.presentation.mapper.toEntity
import com.depromeet.presentation.model.UserProfileUpdateRequest
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.DEFAULT_STRING_VALUE
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UpdateUserProfileViewModel @Inject constructor(
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
): BaseViewModel() {

    val previousUserName: String = checkNotNull(savedStateHandle[KEY_PREVIOUS_USER_NAME])

    private val _updateUserProfileSuccess = MutableSharedFlow<Unit>()
    val updateUserProfileSuccess: SharedFlow<Unit> = _updateUserProfileSuccess.asSharedFlow()

    private val _userName = savedStateHandle.getMutableStateFlow(KEY_USER_NAME, DEFAULT_STRING_VALUE)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _updateUserProfileValidation = MutableStateFlow(false)
    val updateUserValidation: StateFlow<Boolean> = _updateUserProfileValidation.asStateFlow()

    fun updateUserProfile() = viewModelScope.launch {
        updateUserProfileUseCase(UserProfileUpdateRequest(userName.value).toEntity())
            .onEach {result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        showToast(stringResourcesProvider.getString(R.string.user_profile_update_success))
                        _updateUserProfileSuccess.emit(Unit)
                        // btnUpdateMember 활성 상태 초기화
                        setUpdateUserProfileValidation(false)
                    }

                    is Result.Error -> {
                        when {
                            result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail_by_internet_error))
                            }
                            result.statusCode == UNAUTHORIZED -> {
                                navigateToExpireDialog()
                            }
                            else ->showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail))
                        }
                    }
                }
            }
    }

    fun setUserName(userName: String) {
        _userName.value = userName
    }

    fun setUpdateUserProfileValidation(isEnable: Boolean) {
        _updateUserProfileValidation.value = isEnable
    }

    override fun retry() = Unit

    companion object {
        private const val KEY_PREVIOUS_USER_NAME = "previous_user_name"
        private const val KEY_USER_NAME = "user_name"
    }
}