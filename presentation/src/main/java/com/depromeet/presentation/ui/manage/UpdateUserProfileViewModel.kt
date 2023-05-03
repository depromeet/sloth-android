package com.depromeet.presentation.ui.manage

import android.net.Uri
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
import com.depromeet.presentation.util.SERVER_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class UpdateUserProfileViewModel @Inject constructor(
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val previousUserName: String = checkNotNull(savedStateHandle[KEY_PREVIOUS_USER_NAME])
    val previousProfileImageUrl: String = checkNotNull(savedStateHandle[KEY_PROFILE_IMAGE_URL])

    private val _updateUserProfileSuccess = MutableSharedFlow<Unit>()
    val updateUserProfileSuccess: SharedFlow<Unit> = _updateUserProfileSuccess.asSharedFlow()

    private val _userName =
        savedStateHandle.getMutableStateFlow(KEY_USER_NAME, DEFAULT_STRING_VALUE)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _profileImageUrl =
        savedStateHandle.getMutableStateFlow(KEY_PROFILE_IMAGE_URL, Uri.EMPTY)
    val profileImageUrl: StateFlow<Uri> = _profileImageUrl.asStateFlow()

    private val _updateUserProfileValidation = MutableStateFlow(false)
    val updateUserValidation: StateFlow<Boolean> = _updateUserProfileValidation.asStateFlow()

    private val _navigateToPhotoPickerEvent = MutableSharedFlow<Unit>()
    val navigateToPhotoPickerEvent: SharedFlow<Unit> = _navigateToPhotoPickerEvent.asSharedFlow()

    //TODO 사진이 존재하는 경우와 존재하지 않은 경우의 대한 분기 처리
    fun updateUserProfile() = viewModelScope.launch {
        val currentUri = profileImageUrl

        if (currentUri == Uri.EMPTY) {
            updateUserProfileUseCase(
                UserProfileUpdateRequest(
                    userName = userName.value,
                    profileImageUrl = null
                ).toEntity(),
                null
            )
                .onEach { result ->
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
                                result.throwable.message == SERVER_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail_by_server_error))
                                }

                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail_by_internet_error))
                                }

                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }

                                else -> {
                                    showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail))
                                    Timber.tag("updateUserProfile").d(result.throwable)
                                }
                            }
                        }
                    }
                }
        } else {
            updateUserProfileUseCase(
                UserProfileUpdateRequest(
                    userName = userName.value,
                    profileImageUrl = null
                ).toEntity(),
                currentUri.toString()
            )
                .onEach { result ->
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
                                result.throwable.message == SERVER_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail_by_server_error))
                                }

                                result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                    showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail_by_internet_error))
                                }

                                result.statusCode == UNAUTHORIZED -> {
                                    navigateToExpireDialog()
                                }

                                else -> {
                                    showToast(stringResourcesProvider.getString(R.string.user_profile_update_fail))
                                    Timber.tag("updateUserProfile").d(result.throwable)
                                }
                            }
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

    fun navigateToPhotoPicker() = viewModelScope.launch {
        _navigateToPhotoPickerEvent.emit(Unit)
    }

    override fun retry() = Unit

    companion object {
        private const val KEY_PREVIOUS_USER_NAME = "previous_user_name"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_PROFILE_IMAGE_URL = "previous_profile_image_url"
    }
}