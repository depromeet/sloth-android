package com.depromeet.sloth.presentation.screen.manage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.FragmentUpdateMemberDialogBinding
import com.depromeet.sloth.extensions.repeatOnStarted
import com.depromeet.sloth.extensions.safeNavigate
import com.depromeet.sloth.extensions.setEditTextFocus
import com.depromeet.sloth.presentation.screen.base.BaseDialogFragment
import com.depromeet.sloth.util.DebounceEditTextListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UpdateMemberDialogFragment :
    BaseDialogFragment<FragmentUpdateMemberDialogBinding>(R.layout.fragment_update_member_dialog) {

    private val updateMemberViewModel: UpdateMemberViewModel by viewModels()

    private val updateMemberTextChangeListener by lazy {
        DebounceEditTextListener(
            scope = updateMemberViewModel.viewModelScope,
            onDebounceEditTextChange = updateMemberViewModel::setMemberName,
            debouncePeriod = 0L
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {
            vm = updateMemberViewModel
        }
        initViews()
        initObserver()
    }

    override fun initViews() = with(binding) {
        super.initViews()
        focusInputForm(etMemberName)
    }

    private fun initObserver() = with(updateMemberViewModel) {
        repeatOnStarted {
            launch {
                updateMemberSuccess
                    .collect {
                        navigateToManage()
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
                navigateToExpireDialogEvent
                    .collect {
                        showExpireDialog()
                    }
            }


            launch {
                showToastEvent
                    .collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun focusInputForm(editText: EditText) = with(updateMemberViewModel) {
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
                if (memberName.value.isEmpty() || memberName.value == previousMemberName) {
                    setUpdateMemberValidation(false)
                } else {
                    setUpdateMemberValidation(true)
                }
            }
        })
        setEditTextFocus(requireActivity(), editText)
    }

    private fun navigateToManage() {
        val action = UpdateMemberDialogFragmentDirections.actionUpdateMemberDialogToManage()
        findNavController().safeNavigate(action)
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
