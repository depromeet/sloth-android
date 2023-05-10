package com.depromeet.presentation.ui.manage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.depromeet.presentation.R
import com.depromeet.presentation.databinding.FragmentUpdateUserProfileDialogBinding
import com.depromeet.presentation.extensions.repeatOnStarted
import com.depromeet.presentation.extensions.safeNavigate
import com.depromeet.presentation.extensions.setEditTextFocus
import com.depromeet.presentation.ui.base.BaseDialogFragment
import com.depromeet.presentation.util.DebounceEditTextListener
import com.depromeet.presentation.util.GlideApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


//TODO 프로필 이미지를 없애는 기능도 만들어야 할 듯
//TODO 프로필 변경 validation 수정 -> 프로필 사진만 변경 되어서 완료 버튼을 누를 수 있도록
@AndroidEntryPoint
class UpdateUserProfileDialogFragment :
    BaseDialogFragment<FragmentUpdateUserProfileDialogBinding>(R.layout.fragment_update_user_profile_dialog) {

    private val viewModel: UpdateUserProfileViewModel by viewModels()

    private val updateUserProfileTextChangeListener by lazy {
        DebounceEditTextListener(
            scope = viewModel.viewModelScope,
            onDebounceEditTextChange = viewModel::setUserName,
            debouncePeriod = 0L
        )
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Timber.tag("PhotoPicker").d("Selected URI: $uri")
                // 필요한가
                val type = requireContext().contentResolver.getType(uri)?.split("/")?.last() ?: "jpg"
                Timber.tag("PhotoPicker").d("Selected type: $type")
                viewModel.setProfileImageUrl(uri.toString())
                GlideApp.with(requireContext()).load(uri).into(binding.ivUserProfileImage)
            } else {
                Timber.tag("PhotoPicker").d("No media selected")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = viewModel
        }
        initViews()
        initObserver()
    }

    override fun initViews() = with(binding) {
        super.initViews()
        focusInputForm(etUserName)
    }

    private fun initObserver() {
        repeatOnStarted {
            launch {
                viewModel.updateUserProfileSuccess.collect {
                    navigateToManage()
                }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) showProgress() else hideProgress()
                }
            }

            launch {
                viewModel.navigateToExpireDialogEvent.collect {
                    showExpireDialog()
                }
            }


            launch {
                viewModel.showToastEvent.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }

            launch {
                viewModel.navigateToPhotoPickerEvent.collect {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }
    }

    private fun focusInputForm(editText: EditText) = with(viewModel) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i1: Int,
                i2: Int,
                i3: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence?, i1: Int, i2: Int, i3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                if (userName.value.isEmpty() || userName.value == previousUserName) {
                    setUpdateUserProfileValidation(false)
                } else {
                    setUpdateUserProfileValidation(true)
                }
            }
        })
        setEditTextFocus(requireActivity(), editText)
    }

    private fun navigateToManage() {
        val action =
            UpdateUserProfileDialogFragmentDirections.actionUpdateUserProfileDialogToManage()
        findNavController().safeNavigate(action)
    }

    override fun onResume() {
        super.onResume()
        binding.etUserName.addTextChangedListener(updateUserProfileTextChangeListener)
    }

    override fun onPause() {
        binding.etUserName.removeTextChangedListener(updateUserProfileTextChangeListener)
        super.onPause()
    }
}
