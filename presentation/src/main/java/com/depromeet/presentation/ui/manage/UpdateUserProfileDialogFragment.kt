package com.depromeet.presentation.ui.manage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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
        val action = UpdateUserProfileDialogFragmentDirections.actionUpdateUserProfileDialogToManage()
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
