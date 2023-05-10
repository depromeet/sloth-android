package com.depromeet.presentation.ui.manage

import androidx.lifecycle.SavedStateHandle
import com.depromeet.presentation.ui.base.BaseViewModel
import com.depromeet.presentation.util.DEFAULT_STRING_VALUE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class UserProfileDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val profileImageUrl: String = savedStateHandle[KEY_PROFILE_IMAGE_URL] ?: DEFAULT_STRING_VALUE

    override fun retry() = Unit

    companion object {
        private const val KEY_PROFILE_IMAGE_URL = "profile_image_url"
    }
}