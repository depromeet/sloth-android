package com.depromeet.sloth.ui.manage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentUpdateMemberBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.setEditTextFocus
import com.depromeet.sloth.extensions.showForbiddenDialog
import com.depromeet.sloth.extensions.showToast
import com.depromeet.sloth.ui.base.BaseDialogFragment
import com.depromeet.sloth.util.DebounceEditTextListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class UpdateMemberFragment :
    BaseDialogFragment<FragmentUpdateMemberBinding>(R.layout.fragment_update_member) {

    private val manageViewModel: ManageViewModel by hiltNavGraphViewModels(R.id.nav_home)

    private val updateMemberTextChangeListener by lazy {
        DebounceEditTextListener(
            scope = manageViewModel.viewModelScope,
            onDebounceEditTextChange = manageViewModel::setMemberName,
            debouncePeriod = 0L
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            vm = manageViewModel
        }
        initViews()
        initObserver()
    }

    override fun initViews() = with(binding) {
        super.initViews()
        focusInputForm(etMemberName)
    }

    private fun initObserver() = with(manageViewModel) {
        repeatOnStarted {
            launch {
                updateMemberSuccess
                    .collect {
                        closeProfileUpdateDialog()
                    }
            }

            launch {
                isLoading
                    .collect { isLoading ->
                        when (isLoading) {
                            true -> showProgress()
                            false -> hideProgress()
                        }
                    }
            }

            launch {
                showForbiddenDialogEvent
                    .collect {
                        showForbiddenDialog(
                            requireContext(),
                            this@UpdateMemberFragment
                        ) { removeAuthToken() }
                    }
            }


            launch {
                showToastEvent
                    .collect { message ->
                        showToast(requireContext(), message)
                    }
            }
        }
    }

    private fun focusInputForm(editText: EditText) = with(manageViewModel) {
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
                Timber.d(memberName.value)
                if (memberName.value.isEmpty() || memberName.value == previousMemberName.value) {
                    setUpdateMemberValidation(false)
                } else {
                    Timber.d(memberName.value)
                    setUpdateMemberValidation(true)
                }
            }
        })
        setEditTextFocus(editText)
    }

    private fun closeProfileUpdateDialog() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.etMemberName.addTextChangedListener(updateMemberTextChangeListener)
    }

    override fun onPause() {
        binding.etMemberName.removeTextChangedListener(updateMemberTextChangeListener)
        super.onPause()
    }
}
