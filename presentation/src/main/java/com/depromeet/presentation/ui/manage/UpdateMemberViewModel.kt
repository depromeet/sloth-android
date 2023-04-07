package com.depromeet.presentation.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.depromeet.domain.usecase.userprofile.UpdateMemberUseCase
import com.depromeet.domain.util.Result
import com.depromeet.presentation.R
import com.depromeet.presentation.di.StringResourcesProvider
import com.depromeet.presentation.extensions.getMutableStateFlow
import com.depromeet.presentation.mapper.toEntity
import com.depromeet.presentation.model.MemberUpdateRequest
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.DEFAULT_STRING_VALUE
import com.depromeet.presentation.util.INTERNET_CONNECTION_ERROR
import com.depromeet.presentation.util.UNAUTHORIZED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UpdateMemberViewModel @Inject constructor(
    private val updateMemberUseCase: UpdateMemberUseCase,
    private val stringResourcesProvider: StringResourcesProvider,
    savedStateHandle: SavedStateHandle,
): BaseViewModel() {

    val previousMemberName: String = checkNotNull(savedStateHandle[KEY_PREVIOUS_MEMBER_NAME])

    private val _updateMemberSuccess = MutableSharedFlow<Unit>()
    val updateMemberSuccess: SharedFlow<Unit> = _updateMemberSuccess.asSharedFlow()

    private val _memberName = savedStateHandle.getMutableStateFlow(KEY_MEMBER_NAME, DEFAULT_STRING_VALUE)
    val memberName: StateFlow<String> = _memberName.asStateFlow()

    private val _updateMemberValidation = MutableStateFlow(false)
    val updateMemberValidation: StateFlow<Boolean> = _updateMemberValidation.asStateFlow()

    fun updateMemberInfo() = viewModelScope.launch {
        updateMemberUseCase(MemberUpdateRequest(memberName.value).toEntity())
            .onEach {result ->
                setLoading(result is Result.Loading)
            }.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        showToast(stringResourcesProvider.getString(R.string.member_update_success))
                        _updateMemberSuccess.emit(Unit)
                        // btnUpdateMember 활성 상태 초기화
                        setUpdateMemberValidation(false)
                    }

                    is Result.Error -> {
                        when {
                            result.throwable.message == INTERNET_CONNECTION_ERROR -> {
                                showToast(stringResourcesProvider.getString(R.string.member_update_fail_by_internet_error))
                            }
                            result.statusCode == UNAUTHORIZED -> {
                                navigateToExpireDialog()
                            }
                            else ->showToast(stringResourcesProvider.getString(R.string.member_update_fail))
                        }
                    }
                }
            }
    }

    fun setMemberName(memberName: String) {
        _memberName.value = memberName
    }

    fun setUpdateMemberValidation(isEnable: Boolean) {
        _updateMemberValidation.value = isEnable
    }

    override fun retry() = Unit

    companion object {
        private const val KEY_PREVIOUS_MEMBER_NAME = "previous_member_name"
        private const val KEY_MEMBER_NAME = "member_name"
    }
}